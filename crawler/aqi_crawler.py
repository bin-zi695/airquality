import requests
from bs4 import BeautifulSoup
import pymysql
import time
import random
import re
from datetime import datetime

DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "123456",
    "database": "air_quality_db",
    "charset": "utf8mb4",
}

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                  "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8",
}

# 16个城市名 → 拼音映射
CITY_PINYIN = {
    "昆明市": "kunming",
    "曲靖市": "qujing",
    "玉溪市": "yuxi",
    "保山市": "baoshan",
    "昭通市": "zhaotong",
    "丽江市": "lijiang",
    "普洱市": "puer",
    "临沧市": "lincang",
    "楚雄市": "chuxiong",
    "蒙自市": "mengzi",
    "文山市": "wenshan",
    "景洪市": "jinghong",
    "大理市": "dali",
    "芒市": "mangshi",
    "泸水市": "lushui",
    "香格里拉市": "xianggelila",
}

# AQI 月度页面
AQI_URL = "http://www.tianqihoubao.com/aqi/{pinyin}-{year}{month:02d}.html"
# 天气 月度页面
LISHI_URL = "http://www.tianqihoubao.com/lishi/{pinyin}/month/{year}{month:02d}.html"


class AirQualityCrawler:

    def __init__(self):
        self.conn = pymysql.connect(**DB_CONFIG)
        self.cursor = self.conn.cursor()

    def close(self):
        if self.cursor:
            self.cursor.close()
        if self.conn:
            self.conn.close()

    def get_cities(self):
        self.cursor.execute("SELECT id, name FROM city WHERE status = 1 ORDER BY id ASC")
        return self.cursor.fetchall()

    def fetch_page(self, url: str) -> str:
        resp = requests.get(url, headers=HEADERS, timeout=20)
        resp.encoding = "gbk"
        if resp.status_code != 200:
            return ""
        return resp.text

    def fetch_aqi(self, pinyin: str, year: int, month: int) -> dict:
        """拉取AQI月度表格，返回 { '2025-05-01': {aqi, pm25, pm10, ...}, ... }"""
        url = AQI_URL.format(pinyin=pinyin, year=year, month=month)
        html = self.fetch_page(url)
        if not html:
            return {}

        soup = BeautifulSoup(html, "html.parser")
        table = soup.find("table")
        if not table:
            return {}

        rows = table.find_all("tr")
        if len(rows) < 2:
            return {}

        result = {}
        for row in rows[1:]:
            cols = row.find_all("td")
            if len(cols) < 10:
                continue
            try:
                date_str = cols[0].get_text(strip=True)

                def to_int(s):
                    try: return int(s.strip())
                    except: return None

                def to_float(s):
                    try: return float(s.strip())
                    except: return None

                result[date_str] = {
                    "aqi": to_int(cols[1].get_text(strip=True)),
                    "quality": cols[2].get_text(strip=True),
                    "pm25": to_float(cols[4].get_text(strip=True)),
                    "pm10": to_float(cols[5].get_text(strip=True)),
                    "no2": to_float(cols[6].get_text(strip=True)),
                    "so2": to_float(cols[7].get_text(strip=True)),
                    "co": to_float(cols[8].get_text(strip=True)),
                    "o3": to_float(cols[9].get_text(strip=True)),
                }
            except Exception:
                continue

        return result

    def fetch_weather(self, pinyin: str, year: int, month: int) -> dict:
        """拉取天气月度表格，返回 { '2025-05-01': {weather, temperature, wind_speed}, ... }"""
        url = LISHI_URL.format(pinyin=pinyin, year=year, month=month)
        html = self.fetch_page(url)
        if not html:
            return {}

        soup = BeautifulSoup(html, "html.parser")
        table = soup.find("table")
        if not table:
            return {}

        rows = table.find_all("tr")
        if len(rows) < 2:
            return {}

        result = {}
        for row in rows[1:]:
            cols = row.find_all("td")
            if len(cols) < 4:
                continue
            try:
                date_link = cols[0].find("a")
                if date_link:
                    href = date_link.get("href", "")
                    m = re.search(r'/(\d{8})\.html', href)
                    if m:
                        raw_date = m.group(1)
                        date_str = f"{raw_date[:4]}-{raw_date[4:6]}-{raw_date[6:8]}"
                    else:
                        continue
                else:
                    continue

                weather_text = cols[1].get_text(strip=True)
                daytime_weather = weather_text.split("/")[0].strip() if "/" in weather_text else weather_text

                temp_text = cols[2].get_text(strip=True)
                max_temp = None
                if "℃" in temp_text:
                    parts = temp_text.split("/")
                    if parts:
                        max_str = parts[0].strip().replace("℃", "").strip()
                        try: max_temp = float(max_str)
                        except: pass

                wind_text = cols[3].get_text(strip=True)
                max_wind = None
                if "级" in wind_text:
                    daytime_wind = wind_text.split("/")[0].strip()
                    levels = re.findall(r'(\d+)级', daytime_wind)
                    if levels:
                        max_wind = max(int(lvl) for lvl in levels)

                # 提取白天部分的风力数字作为 wind_speed
                wind_speed_str = None
                if "级" in wind_text:
                    daytime_part = wind_text.split("/")[0].strip()
                    wind_speed_str = daytime_part.split(" ")[-1] if " " in daytime_part else daytime_part
                    # 简化：取"北风 1-3级" → "1-3级"

                result[date_str] = {
                    "weather": daytime_weather,
                    "temperature": max_temp,
                    "wind_speed": wind_speed_str,
                }
            except Exception:
                continue

        return result

    def save_all(self, city_id: int, aqi_data: dict, weather_data: dict) -> int:
        """合并AQI和天气数据，批量入库"""
        all_dates = set(aqi_data.keys()) | set(weather_data.keys())
        if not all_dates:
            return 0

        sql = """
            INSERT INTO air_quality_data
            (city_id, date, aqi, pm25, pm10, so2, no2, co, o3, temperature, wind_speed, weather)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
            aqi = VALUES(aqi), pm25 = VALUES(pm25), pm10 = VALUES(pm10),
            so2 = VALUES(so2), no2 = VALUES(no2), co = VALUES(co), o3 = VALUES(o3),
            temperature = VALUES(temperature), wind_speed = VALUES(wind_speed), weather = VALUES(weather)
        """
        count = 0
        for d in sorted(all_dates):
            aqi = aqi_data.get(d, {})
            w = weather_data.get(d, {})
            try:
                self.cursor.execute(sql, (
                    city_id, d,
                    aqi.get("aqi"), aqi.get("pm25"), aqi.get("pm10"),
                    aqi.get("so2"), aqi.get("no2"), aqi.get("co"), aqi.get("o3"),
                    w.get("temperature"), w.get("wind_speed"), w.get("weather"),
                ))
                count += 1
            except Exception as e:
                print(f"    入库失败 {d}: {e}")

        self.conn.commit()
        return count

    def run_monthly(self, year: int, month: int):
        cities = self.get_cities()
        total_aqi = 0
        total_weather = 0

        print(f"\n{'='*60}")
        print(f"  空气质量爬虫 — {year}年{month:02d}月")
        print(f"  数据源：AQI(tianqihoubao.com/aqi) + 天气(tianqihoubao.com/lishi)")
        print(f"  待采集城市: {len(cities)} 个")
        print(f"{'='*60}\n")

        for city_row in cities:
            city_id, city_name = city_row
            pinyin = CITY_PINYIN.get(city_name)
            if not pinyin:
                print(f"  📍 {city_name:<10s} ⚠ 无拼音映射，跳过")
                continue

            print(f"  📍 {city_name:<10s}", end=" ")

            aqi_data = self.fetch_aqi(pinyin, year, month)
            if aqi_data:
                print(f"AQI:{len(aqi_data)}天", end=" ")
            else:
                print(f"AQI:无", end=" ")

            time.sleep(random.uniform(0.5, 1.5))

            weather_data = self.fetch_weather(pinyin, year, month)
            if weather_data:
                print(f"天气:{len(weather_data)}天", end=" → ")
            else:
                print(f"天气:无", end=" → ")

            count = self.save_all(city_id, aqi_data, weather_data)
            if count:
                total_aqi += len(aqi_data)
                total_weather += len(weather_data)
                print(f"✅ 入库{count}条")
            else:
                print(f"❌ 无数据")

            time.sleep(random.uniform(0.5, 1.5))

        print(f"\n  📊 AQI {total_aqi}条 + 天气 {total_weather}条\n")

    def run_current_month(self):
        now = datetime.now()
        self.run_monthly(now.year, now.month)


if __name__ == "__main__":
    crawler = AirQualityCrawler()
    try:
        crawler.run_current_month()
    finally:
        crawler.close()
