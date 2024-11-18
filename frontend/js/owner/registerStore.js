new Vue({
  el: "#app",
  data: {
    storeDTO: {
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
    scheduleDTO: [
      { day: "일요일", open: "", close: "", closeDay: false, dayCodeId: 0 },
      { day: "월요일", open: "", close: "", closeDay: false, dayCodeId: 1 },
      { day: "화요일", open: "", close: "", closeDay: false, dayCodeId: 2 },
      { day: "수요일", open: "", close: "", closeDay: false, dayCodeId: 3 },
      { day: "목요일", open: "", close: "", closeDay: false, dayCodeId: 4 },
      { day: "금요일", open: "", close: "", closeDay: false, dayCodeId: 5 },
      { day: "토요일", open: "", close: "", closeDay: false, dayCodeId: 6 },
    ],
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
    detailAddress: "",
    storeCategoryDTO: [],
    geocoder: null,
    fileCount: 0,
  },
  mounted() {
    // Vue 인스턴스가 생성된 후 Geocoder 객체 초기화
    this.geocoder = new kakao.maps.services.Geocoder();
  },
  created() {
    this.checkLogin();
  },
  methods: {
    transName(fileName) {
      // 파일 경로에서 파일명 추출
      const fullName = fileName.split("/").pop();

      // 파일명에서 언더스코어 이후의 부분 추출
      const titleWithExtension = fullName.split("_").pop();

      return titleWithExtension;
    },

    handleImageUpload(event) {
      const files = event.target.files;

      // 이미 5개 이상의 파일이 있을 경우 더 이상 추가하지 않음
      if (this.uploadedFiles.length + files.length > 5) {
        alert("최대 5개의 파일만 업로드 가능합니다.");
        return;
      }

      // 새로운 파일들을 배열에 추가하고 url2 값을 설정
      Array.from(files).forEach((file) => {
        // 업로드 파일 객체를 준비
        const fileObj = file;

        // 새로 추가된 파일인 경우 URL.createObjectURL을 사용하여 미리보기 URL 생성
        fileObj.url2 = URL.createObjectURL(file);

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
      this.uploadedFiles.splice(index, 1); // 파일 삭제
      this.updateFileCount(); // 파일 갯수 업데이트
    },

    // 파일 갯수 갱신하는 메서드
    updateFileCount() {
      this.fileCount = this.uploadedFiles.length; // 파일 갯수 업데이트
    },

    // 파일 갯수를 갱신하는 메서드
    updateFileCount() {
      this.fileCount = this.uploadedFiles.length; // 파일 갯수를 업데이트
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
            document.getElementById("sample6_extraAddress").value = extraAddr;
          } else {
            document.getElementById("sample6_extraAddress").value = "";
          }
          document.getElementById("sample6_postcode").value = data.zonecode;
          document.getElementById("sample6_address").value = addr;
          document.getElementById("sample6_detailAddress").focus();
          this.detailAddress = document.getElementById(
            "sample6_detailAddress"
          ).value;
          this.storeDTO.sidoCode = data.sido;
          this.storeDTO.address = addr;
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
    checkLogin: function () {
      axios
        .get("/api/status")
        .then((response) => {
          if (response.data.data.loginType == "user") {
            location.href = "/";
          } else {
            if (response.status == 200) {
              alert("이미 등록하셨습니다");
              location.href = "/view/owner/index";
            }
          }
        })
        .catch((error) => {
          if (error.status == 401) {
            alert("로그인 정보가 없습니다.");
            location.href = "/";
          }
        });
    },
    // 가게 등록 함수
    registerStore() {
      document.getElementById("loadingSpinner").style.display = "block";

      var detailAddress = document.getElementById(
        "sample6_detailAddress"
      ).value;
      this.storeDTO.address += " " + detailAddress;
      let formData = new FormData();

      this.uploadedFiles.forEach((file) => {
        formData.append("uploadedFiles", file); // 파일 객체를 formData에 추가
      });

      formData.append(
        "storeDTO",
        new Blob([JSON.stringify(this.storeDTO)], { type: "application/json" })
      );
      formData.append(
        "scheduleDTO",
        new Blob([JSON.stringify(this.scheduleDTO)], {
          type: "application/json",
        })
      );
      formData.append(
        "storeCategoryDTO",
        new Blob([JSON.stringify(this.storeCategoryDTO)], {
          type: "application/json",
        })
      );
      formData.append(
        "facilityDTO",
        new Blob([JSON.stringify(this.facilityDTO)], {
          type: "application/json",
        })
      );
      axios
        .post("/api/store", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          alert("가게 등록이 완료되었습니다.");
          this.resetForm();
          location.href = "/view/owner/index";
        })
        .catch((error) => {
          alert("가게 등록에 실패했습니다.");
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
      this.detailAddress = "";
      this.storeCategoryDTO = [];
    },
  },
});
