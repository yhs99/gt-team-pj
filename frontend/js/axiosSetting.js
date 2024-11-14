// Axios 인터셉터 설정
axios.interceptors.request.use(function (config) {
  // 요청이 시작되면 스피너를 보여줌
  document.getElementById('spinner').classList.add('show');
  return config;
}, function (error) {
  // 요청 중 에러가 발생하면 스피너를 숨김
  document.getElementById('spinner').classList.remove('show');
  return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
  // 응답이 성공하면 스피너를 숨김
  document.getElementById('spinner').classList.remove('show');
  return response;
}, function (error) {
  // 응답에서 에러가 발생해도 스피너를 숨김
  document.getElementById('spinner').classList.remove('show');
  return Promise.reject(error);
});
