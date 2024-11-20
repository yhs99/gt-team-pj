new Vue({
  el: "#app",
  data: {
    storeDTO: {
      storeId: 0,
      ownerId: 0,
      rotationId: 0,
      sidoCodeId: 0,
      sidoCode: "",
      storeName: "",
      address: "",
      tel: "",
      description: "",
      directionGuide: "",
      maxPeople: null,
      maxPeoplePerReserve: null,
      locationLatX: null,
      locationLonY: null,
    },
    scheduleDTO: [],
    storeCategory: [
      { categoryId: "C1", categoryName: "한식" },
      { categoryId: "C2", categoryName: "일식" },
      { categoryId: "C3", categoryName: "중식" },
      { categoryId: "C4", categoryName: "아시아식" },
      { categoryId: "C5", categoryName: "서양식" },
      { categoryId: "C6", categoryName: "스시, 초밥" },
      { categoryId: "C7", categoryName: "육류" },
      { categoryId: "C8", categoryName: "오마카세" },
    ],
    storeFacility: [
      { facilityCode: "A1", facilityName: "주차 가능" },
      { facilityCode: "A2", facilityName: "발렛 가능" },
      { facilityCode: "A3", facilityName: "웰컴 키즈존" },
      { facilityCode: "A4", facilityName: "장애인 편의시설" },
      { facilityCode: "A5", facilityName: "단체 이용 가능" },
      { facilityCode: "A6", facilityName: "아기 의자" },
    ],
    storeRotaion: [
      { rotationCodeId: 1, rotationTime: 30 },
      { rotationCodeId: 2, rotationTime: 60 },
      { rotationCodeId: 3, rotationTime: 90 },
      { rotationCodeId: 4, rotationTime: 120 },
      { rotationCodeId: 5, rotationTime: 150 },
      { rotationCodeId: 6, rotationTime: 180 },
    ],
    facilityDTO: [],
    uploadedFiles: [],
    storeCategoryDTO: [],
    geocoder: null,
    fileCount: 0,
    saveAddr: "",
    zipCode: "",
    deletedImageUrls: [],
    doroAddress: "",
    addressWithoutDetail: "",
    address: "",
    detailAddress: "",
    extraAddress: "",
    beforeAddress: "",
    afterAddress: "",
  },
  created: async function () {
    await this.fetchStores();
    this.fetchAddressFromKakao();
    this.updateFileCount();
  },
  mounted() {
    // Vue 인스턴스가 생성된 후 Geocoder 객체 초기화
    this.geocoder = new kakao.maps.services.Geocoder();
  },
  methods: {
    transName(fileName) {
      // 파일 경로에서 파일명 추출
      const fullName = fileName.split("/").pop();

      // 파일명에서 언더스코어 이후의 부분 추출
      const titleWithExtension = fullName.split("_").pop();

      return titleWithExtension;
    },
    async fetchStores() {
      try {
        // axios를 이용해 store 데이터를 가져옵니다.
        const response = await axios.get("/api/store");
        const storeData = response.data.data;

        this.storeDTO = storeData.store;  // storeDTO에 응답 데이터 저장
        this.address = this.storeDTO.address; // 주소 초기화
        this.scheduleDTO = storeData.schedule;
        this.facilityDTO = storeData.facility;
        this.storeCategoryDTO = storeData.category;
        this.uploadedFiles = storeData.storeImage;

      } catch (error) {
        alert('fetchStores 가게 정보 수정 중 오류가 발생했습니다.');
      }
    },

    fetchAddressFromKakao() {
      const address = this.storeDTO.address;

      const geocoder = new kakao.maps.services.Geocoder();
      geocoder.addressSearch(address, (result, status) => {
        if (status === kakao.maps.services.Status.OK) {
          this.storeDTO.sidoCode = result[0].address.region_1depth_name;
          this.zipCode = result[0].road_address.zone_no;
          this.beforeAddress = address;
          this.address = result[0].address_name;
          this.detailAddress = address
            .replace(result[0].address_name, "")
            .trim();
        } else {
          console.log("주소 검색 실패");
        }
      });
    },

    handleImageUpload(event) {
      const files = event.target.files;

      // 이미 5개 이상의 파일이 있을 경우 더 이상 추가하지 않음
      if (this.uploadedFiles.length + files.length > 5) {
        alert("최대 5개의 파일만 업로드 가능합니다.");
        return;
      }

        // 새로운 파일들을 배열에 추가하고 url2 값을 설정
      Array.from(files).forEach(file => {
        // 업로드 파일 객체를 준비
        const fileObj = file; // 파일 이름

        // 만약 file 객체에 url이 있는 경우
        if (file.url) {
          fileObj.url2 = file.url; // 서버에서 받은 url을 url2에 할당
        } else {
          // 새로 추가된 파일인 경우 URL.createObjectURL을 사용하여 미리보기 URL 생성
          fileObj.url2 = URL.createObjectURL(file);
        }

        // 파일을 uploadedFiles 배열에 추가
        this.uploadedFiles.push(fileObj);
      });

      // 파일 갯수 업데이트
      this.updateFileCount();
    },

    // 파일 선택 버튼 클릭 시 input[type="file"]을 트리거하는 메서드
    triggerFileInput() {
      document.getElementById("imageUpload").click();
    },

    // 파일 삭제 처리
    removeFile(index) {
      const file = this.uploadedFiles[index];

      // file.fileName이 있는 경우 deletedImageUrls에 추가
      if (file.fileName) {
        this.deletedImageUrls.push(file.fileName);
      }

      // uploadedFiles 배열에서 해당 파일 제거
      this.uploadedFiles.splice(index, 1); // 파일 삭제
      this.updateFileCount(); // 파일 갯수 업데이트
    },

    // 파일 갯수 갱신하는 메서드
    updateFileCount() {
      this.fileCount = this.uploadedFiles.length; // 파일 갯수 업데이트
    },

    // 예약 시간 주기 토글
    toggleRotation(rotation) {
      if (this.storeDTO.rotationId === rotation.rotationCodeId) {
        this.storeDTO.rotationId = 0; // 이미 선택된 경우 해제
      } else {
        this.storeDTO.rotationId = rotation.rotationCodeId; // 선택된 경우 설정
      }
    },
    rotationIsSelected(rotation) {
      return this.storeDTO.rotationId === rotation.rotationCodeId;
    },
    // 편의시설 선택 토글 메서드
    toggleFacility(facility) {
      const index = this.facilityDTO.findIndex(
        (f) => f.facilityCode === facility.facilityCode
      );
      if (index === -1) {
        this.facilityDTO.push({ facilityCode: facility.facilityCode });
      } else {
        this.facilityDTO.splice(index, 1);
      }
    },
    facilityIsSelected(facility) {
      return this.facilityDTO.some(
        (f) => f.facilityCode === facility.facilityCode
      );
    },
    // 카테고리 선택 토글 메서드
    toggleCategory(category) {
      const index = this.storeCategoryDTO.findIndex(
        (c) => c.categoryCodeId === category.categoryId
      );
      if (index === -1) {
        this.storeCategoryDTO.push({
          categoryCodeId: category.categoryId,
          storeCategoryName: category.categoryName,
        });
      } else {
        this.storeCategoryDTO.splice(index, 1);
      }
    },
    isSelected(category) {
      return this.storeCategoryDTO.some(
        (c) => c.categoryCodeId === category.categoryId
      );
    },
    // Daum 우편번호 API를 이용해 주소 검색
    sample6_execDaumPostcode() {
      new daum.Postcode({
        oncomplete: (data) => {
          var addr = "";
          var extraAddr = "";

          if (data.userSelectedType === "R") {
            addr = data.roadAddress;
          } else {
            addr = data.jibunAddress;
          }

          if (data.userSelectedType === "R") {
            if (data.bname !== "" && /[동|로|가]$/g.test(data.bname)) {
              extraAddr += data.bname;
            }
            if (data.buildingName !== "" && data.apartment === "Y") {
              extraAddr +=
                extraAddr !== "" ? ", " + data.buildingName : data.buildingName;
            }
            if (extraAddr !== "") {
              extraAddr = " (" + extraAddr + ")";
            }
            this.extraAddress = extraAddr;
          } else {
            this.extraAddress = "";
          }

          this.zipCode = data.zonecode;
          this.address = addr;
          this.afterAddress = addr;

          // 상세 주소 입력란에 포커스
          this.$nextTick(() => {
            this.$refs.detailAddress.focus();
          });

          this.storeDTO.sidoCode = data.sido;

          // Geocoder를 사용하여 좌표를 얻음
          this.geocoder.addressSearch(addr, (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
              this.storeDTO.locationLatX = parseFloat(result[0].x).toFixed(7);
              this.storeDTO.locationLonY = parseFloat(result[0].y).toFixed(7);
            }
          });
        },
      }).open();
    },
    // 운영/휴무 상태 변경 시 처리
    toggleScheduleVisibility(index) {
      const schedule = this.scheduleDTO[index];
      if (schedule.closeDay) {
        schedule.close = "";
      }
    },
    // 가게 수정 함수
    updateStore() {
      document.getElementById("loadingSpinner").style.display = "block";
      let formData = new FormData();
      this.storeDTO.address = document.getElementById('sample6_address').value + ' ' + document.getElementById('sample6_detailAddress').value
      this.uploadedFiles.forEach(file => {
        formData.append('uploadedFiles', file);  // 파일 객체를 formData에 추가
      });

      formData.append('storeDTO', new Blob([JSON.stringify(this.storeDTO)], { type: "application/json" }));
      formData.append('scheduleDTO', new Blob([JSON.stringify(this.scheduleDTO)], { type: "application/json" }));
      formData.append('storeCategoryDTO', new Blob([JSON.stringify(this.storeCategoryDTO)], { type: "application/json" }));
      formData.append('facilityDTO', new Blob([JSON.stringify(this.facilityDTO)], { type: "application/json" }));
      formData.append('deletedImageUrls', new Blob([JSON.stringify(this.deletedImageUrls)], { type: "application/json" }));

      axios.put(`/api/store/${this.storeDTO.storeId}`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
        .then(response => {
          alert('가게 수정이 완료되었습니다.');
          location.href = "/";
        })
        .catch(error => {
          alert('가게 수정에 실패했습니다.');
        })
        .finally(() => {
          // 로딩 스피너 숨기기
          document.getElementById("loadingSpinner").style.display = "none";
        });
    },
    // 양식 초기화
    resetForm() {
      this.storeDTO = {
        ownerId: 0,
        rotationId: 0,
        sidoCodeId: 0,
        sidoCode: "",
        storeName: "",
        address: "",
        tel: "",
        description: "",
        directionGuide: "",
        maxPeople: null,
        maxPeoplePerReserve: null,
        locationLatX: null,
        locationLonY: null,
      };
      this.scheduleDTO = [
        { day: "월요일", open: "", close: "", closeDay: false, dayCodeId: 1 },
        { day: "화요일", open: "", close: "", closeDay: false, dayCodeId: 2 },
        { day: "수요일", open: "", close: "", closeDay: false, dayCodeId: 3 },
        { day: "목요일", open: "", close: "", closeDay: false, dayCodeId: 4 },
        { day: "금요일", open: "", close: "", closeDay: false, dayCodeId: 5 },
        { day: "토요일", open: "", close: "", closeDay: false, dayCodeId: 6 },
        { day: "일요일", open: "", close: "", closeDay: false, dayCodeId: 0 },
      ];
      this.facilityDTO = [];
      this.doroAddress = "";
      this.storeCategoryDTO = [];
    },
  },
});
