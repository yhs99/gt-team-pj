new Vue({
  el: "#app",
  data: {
    submit: false,
    formDatas: {
      id: '',
      password: '',
      password_re: ''
    },
    errorMsg: {
      errorCode:[],
      errorMsg:[]
    },
    previewImage: '/assets/defaultUser.jpg'
  },
  created: function () {
    this.checkLogin();
  },
  methods: {
    checkLogin: function () {
      axios
        .get("/api/status")
        .then((response) => {
          if (response.data.data.loginType == "user") {
            location.href = "/";
          } else {
            location.href = "/view/owner/index";
          }
        })
        .catch((error) => {
          if (!error.status == 401) {
            return;
          }
        });
    },
    async register() {
      if(this.validate()){
        const formData = new FormData();
        formData.append('id', this.formDatas.id);
        formData.append('password', this.formDatas.password);
        await axios.post('/api/store/owner/register', formData)
        .then(response => {
          alert('점주 회원가입이 완료되었습니다. 가게 정보를 등록해주세요!');
          location.href = '/view/owner/registerStore';
        })
        .catch(error => {
          if(error.status === 400) {
            alert(error.response.data.message);
          }else {
            alert('회원가입중 에러가 발생했습니다. 다음에 다시 시도해주세요');
          }
        })
      }
    },
    validate: function() {
      let r = true;
      this.errorMsg = {
        errorCode:[],
        errorMsg:[]
      };
      const errors = this.errorMsg;
      return this.validateId(this.formDatas.id, errors)
            && this.validatePassword(this.formDatas.password, this.formDatas.password_re, errors);
    },
    validateId(id, errors) {
      let r = true;
      if(id.length === 0) {
        errors.errorCode.push('id');
        errors.errorMsg.push('아이디를 입력해주세요.');
        r = false;
      }
      return r;
    },
    validatePassword(password, password_re, errors) {
      let r = true;
      if(password !== password_re) {
        errors.errorCode.push('password_re');
        errors.errorMsg.push('비밀번호가 일치하지 않습니다.');
        r = false;
      }
      if(password.length < 8) {
        errors.errorCode.push('password');
        errors.errorMsg.push('비밀번호는 8자 이상이어야합니다.');
        r = false;
      }
      return r;
    }
  },
});
