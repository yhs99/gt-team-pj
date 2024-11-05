new Vue({
  el: '#app',
  data: {
    updateFlag: true,
    userModal: null,
    previewImage: null,
    uploadImage: null,
    currentPage: 1, // 현재 페이지
    pageSize: 10, // 페이지당 표시 갯수
    userCount: 0, // 검색 총 결과수
    totalPages: 0, // 총 페이지
    userLists: [],
    userInfo:{},
    selectedFilter:'userId',
    selectedFilterName:'회원ID',
    filters: [
      {
        filter : 'userId',
        filterName : '회원ID'
      },
      {
        filter : 'email',
        filterName : '이메일'
      },
      {
        filter : 'name',
        filterName : '이름'
      }
    ],
    enterQuery:'',
  },
  created: function() {
    this.fetchUsers();
  },
  mounted: function() {
    this.userModal = new bootstrap.Modal(document.getElementById("userModal"));
  },
  watch: {
    selectedFilter: 'fetchUsers',
    enterQuery: 'fetchUsers'
  },
  methods: {
    fetchUsers: async function() {
      let filter = {};
      if(this.enterQuery != '') {
        filter.filterName = this.selectedFilter;
        filter.filterQuery = this.enterQuery;
      }
      const params = new URLSearchParams(filter);
      await axios.get('/api/admin/users', {params})
      .then(response => {
        this.userCount = response.data.data.userCount;
        this.userLists = response.data.data.userLists;
        this.totalStoreCount = this.userCount;
        this.totalPages = Math.ceil(this.totalStoreCount / this.pageSize);
      })
      .catch(error => {
        alert('유저 정보 로딩중 오류가 발생했습니다.');
        console.error(error);
      })
    },
    openModalForUpdate(userId) {
      axios.get(`/api/admin/users/${userId}`)
      .then(response => {
        this.userInfo = response.data.data;
        this.previewImage = this.userInfo.profileImageUrl;
        this.uploadImage = null;
        this.$refs.fileInput.value = '';
        this.updateFlag = true;
        this.userModal.show();
      })
      .catch(error => {
        alert('유저 정보 로딩중 오류가 발생했습니다.');
        console.error(error);
      });
    },
    onFileChange(event) {
      const file = event.target.files[0];
      if (file && file.type.startsWith("image/")) {
        this.uploadImage = file;
        console.log(this.uploadImage);
        const reader = new FileReader();

        reader.onload = (e) => {
          this.previewImage = e.target.result;
        };
        reader.readAsDataURL(file);
      } else {
        alert("이미지 파일을 선택하세요.");
      }
    },
    submitUpdate: async function() {
      try {
        const formData = new FormData();
        formData.append('email', this.userInfo.email);
        formData.append('password', this.userInfo.password);
        formData.append('gender', this.userInfo.gender);
        formData.append('mobile', this.userInfo.mobile);
        formData.append('name', this.userInfo.name);
        if(this.uploadImage != null) {
          formData.append('uploadImage', this.uploadImage);
        }
        const response = await axios.patch(`/api/admin/users/${this.userInfo.userId}`, formData)
        alert(response.data.data);
        this.userModal.hide();
        this.fetchUsers();
      }catch(error) {
        alert(error.message);
        console.error(error);
      }
    },
    changePage: function(page) {
      this.currentPage = page;
    },
  },
  computed: {
    paginatedUsers() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.userLists.slice(start, end);
    },
    getFilterName() {
      this.selectedFilterName = this.filters.find(f => f.filter == this.selectedFilter).filterName;
    }
  }
})