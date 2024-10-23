new Vue({
    el: "#inputBox",
    data: {
        id: '',
        password: '',
        loginGroup: document.getElementById('loginGroup').value,
        idError: ''
    },
    methods: {
        validateId() {
            const idPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
            if (!idPattern.test(this.id)) {
                this.idError = '유효하지 않은 아이디 형식입니다.';
                return false;
            }
            this.idError = '';
            return true;
        },
        async login() {
            if (this.validateId()) {
                const formData = new FormData();
                formData.append('id', this.id);
                formData.append('password', this.password);
                formData.append('loginGroup', this.loginGroup);
                try {
                  const response = await axios.post('/api/login', formData);
                    console.log(response);
                    if (response.status === 200) {
                        console.log("로그인 성공");
                    } 
                } catch (error) {
                    console.error('API 요청 실패:', error.response.data.message);
                    this.idError=error.response.data.message;
                }
            }
        }
    }
});