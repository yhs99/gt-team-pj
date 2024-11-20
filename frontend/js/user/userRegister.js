new Vue({
  el: "#app",
  data: {
    submit: false,
    socialRegister: false,
    formDatas: {
      email: '',
      password: '',
      password_re: '',
      name: '',
      mobile: '',
      gender: 'M',
      imageFile:null
    },
    errorMsg: {
      errorCode:[],
      errorMsg:[]
    },
    previewImage: '/assets/defaultUser.jpg'
  },
  created: function () {
    this.checkLogin();
    this.searchEmail();
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
        const fileInput = document.querySelector('input[name="imageFile"]');
        const file = fileInput.files[0];
        const formData = new FormData();
        formData.append('email', this.formDatas.email!=''?this.formDatas.email:null);
        formData.append('password', this.formDatas.password!=''?this.formDatas.password:null);
        formData.append('name', this.formDatas.name!=''?this.formDatas.name:null);
        formData.append('mobile', this.formDatas.mobile!=''?this.formDatas.mobile:null);
        formData.append('gender', this.formDatas.gender!=''?this.formDatas.gender:'M');
        if(this.formDatas.imageFile) {
          formData.append('imageFile', this.formDatas.imageFile);
        }

        await axios.post('/api/user/register', formData)
        .then(response => {
          alert(response.data.data);
          location.href = '/view/user/userLogin';
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
      return this.validateEmail(this.formDatas.email, errors)
            && this.validatePassword(this.formDatas.password, this.formDatas.password_re, errors)
            && this.validateName(this.formDatas.name, errors)
            && this.validateMobile(this.formDatas.mobile, errors);
    },
    validateEmail(email, errors) {
      let r = true;
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if(!emailRegex.test(email)) {
        errors.errorCode.push('email');
        errors.errorMsg.push('이메일 주소를 확인해주세요.');
        r = false;
      }else if(email.length === 0) {
        errors.errorCode.push('email');
        errors.errorMsg.push('이메일 주소를 입력해주세요.');
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
    },
    validateName(name, errors) {
      let r = true;
      const koreanRegex = /^[가-힣]+$/;
      if(name.length == 0) {
        errors.errorCode.push('name');
        errors.errorMsg.push('이름을 입력해주세요.');
        r = false;
      }else if(!koreanRegex.test(name)) {
        errors.errorCode.push('name');
        errors.errorMsg.push('이름은 한글만 입력가능합니다.');
        r = false;
      }
      return r;
    },
    validateMobile(mobile, errors) {
      let r = true;
      const phoneRegex = /^(?:\d{2,3}-\d{3,4}-\d{4})$/;
      if(mobile.length !== 0 && !phoneRegex.test(mobile)) {
        errors.errorCode.push('mobile');
        errors.errorMsg.push('휴대폰 번호는 010-1234-5678 형식으로 입력해주세요.');
        r = false;
      }
      return r;
    },
    onFileChange(event) {
      const file = event.target.files[0];
      if (file && file.type.startsWith("image/")) {
        this.formDatas.imageFile = file;
        const reader = new FileReader();

        reader.onload = (e) => {
          this.previewImage = e.target.result;
        };
        reader.readAsDataURL(file);
      } else {
        alert("이미지 파일을 선택하세요.");
      }
    },
    searchEmail() {
      const email = new URLSearchParams(document.location.search).get("email");
      if(email != null) {
        alert("서비스 이용을 위해 회원가입을 진행해주세요!");
        this.formDatas.email = email;
        this.socialRegister = true;
      }
    }
  },
});
