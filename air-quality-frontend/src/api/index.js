import request from './request'

export const authApi = {
  login(data) { return request.post('/auth/login', data) },
  register(data) { return request.post('/auth/register', data) },
}

export const userApi = {
  getById(id) { return request.get(`/users/${id}`) },
  list(params) { return request.get('/users', { params }) },
  update(id, data) { return request.put(`/users/${id}`, data) },
  updateStatus(id, status) { return request.put(`/users/${id}/status`, null, { params: { status } }) },
  delete(id) { return request.delete(`/users/${id}`) },
}

export const cityApi = {
  listAll() { return request.get('/cities/all') },
  list(params) { return request.get('/cities', { params }) },
  getById(id) { return request.get(`/cities/${id}`) },
  save(data) { return request.post('/cities', data) },
  update(id, data) { return request.put(`/cities/${id}`, data) },
  delete(id) { return request.delete(`/cities/${id}`) },
}

export const airDataApi = {
  getById(id) { return request.get(`/air-data/${id}`) },
  list(params) { return request.get('/air-data', { params }) },
  getLatestByCity(cityId) { return request.get('/air-data/latest', { params: { cityId } }) },
  getLatestByFavorites(userId) { return request.get('/air-data/favorites-latest', { params: { userId } }) },
  getFavoriteDates(userId) { return request.get('/air-data/favorites-dates', { params: { userId } }) },
  getFavoritesByDate(userId, date) { return request.get('/air-data/favorites-by-date', { params: { userId, date } }) },
  getAllDates() { return request.get('/air-data/all-dates') },
  getAllByDate(date) { return request.get('/air-data/all-by-date', { params: { date } }) },
  getTrend(params) { return request.get('/air-data/trend', { params }) },
  getAqiLevel(aqi) { return request.get(`/air-data/aqi-level/${aqi}`) },
  save(data) { return request.post('/air-data', data) },
  batchSave(data) { return request.post('/air-data/batch', data) },
  update(id, data) { return request.put(`/air-data/${id}`, data) },
  delete(id) { return request.delete(`/air-data/${id}`) },
}

export const favoriteApi = {
  getCityIds(userId) { return request.get('/favorites/cities', { params: { userId } }) },
  count(userId) { return request.get('/favorites/count', { params: { userId } }) },
  isFavorite(userId, cityId) { return request.get('/favorites/check', { params: { userId, cityId } }) },
  add(userId, cityId) { return request.post('/favorites', null, { params: { userId, cityId } }) },
  remove(userId, cityId) { return request.delete('/favorites', { params: { userId, cityId } }) },
}

export const alertApi = {
  listAll() { return request.get('/alert-thresholds') },
  getEnabled() { return request.get('/alert-thresholds/enabled') },
  getById(id) { return request.get(`/alert-thresholds/${id}`) },
  save(data) { return request.post('/alert-thresholds', data) },
  update(id, data) { return request.put(`/alert-thresholds/${id}`, data) },
  delete(id) { return request.delete(`/alert-thresholds/${id}`) },
}

export const operationLogApi = {
  list(params) { return request.get('/operation-logs', { params }) },
}

export const accessLogApi = {
  list(params) { return request.get('/access-logs', { params }) },
}

export const articleApi = {
  listPublished() { return request.get('/articles/published') },
  list(params) { return request.get('/articles', { params }) },
  getById(id) { return request.get(`/articles/${id}`) },
  save(data) { return request.post('/articles', data) },
  update(id, data) { return request.put(`/articles/${id}`, data) },
  delete(id) { return request.delete(`/articles/${id}`) },
}

export const configApi = {
  getByKey(key) { return request.get(`/config/${key}`) },
}
