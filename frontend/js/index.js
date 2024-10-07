new Vue({
  el: '#app',
  data: {
      userId: "",
      id: "",
      title: "",
      completed: ""
  },
  created() {
      this.fetchData(); // 컴포넌트가 생성될 때 데이터 가져오기
  },
  methods: {
      fetchData() {
          // 외부 API 호출
          axios.get('https://jsonplaceholder.typicode.com/todos/1')
                .then(response => {
                    console.log(response);
                    this.userId = response.data.userId;
                    this.id = response.data.id;
                    this.title = response.data.title;
                    this.completed = response.data.completed;
                })
                .catch(error => {
                    console.log(error);
                })
      }
  }
});
