new Vue({
    el: '#userHeader',
    data() {
        return {
            isLoggedIn: false // 초기 로그인 상태 설정
        };
    },
    created() {
        this.checkLoginStatus(); // 로그인 상태 확인
    },
    methods: {
        checkLoginStatus() {
            // 세션에서 로그인 상태를 확인하는 로직
            this.isLoggedIn = sessionStorage.getItem('loggedIn') === 'true';
        }
    }
});