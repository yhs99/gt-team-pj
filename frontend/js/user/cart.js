new Vue({
  el: "#app",
  data: {
    carts: [],
    checkedCarts: [],
    reservationName: "",
    reservationNameError: "",
    peopleCount: 1,
    peopleCountError: "",
    note: "",
    isModalOpen: false,
    globalCoupons: [],
    selectedCoupon: null,
    discountAmount: 0,
    maxPeoplePerReserve: 0,
    selectedPayment: "",
  },
  computed: {
    groupedCarts() {
      if (!Array.isArray(this.carts) || this.carts.length === 0) {
        return {};
      }
      return this.carts.reduce((groups, cart) => {
        if (cart && cart.storeName) {
          if (!groups[cart.storeName]) {
            groups[cart.storeName] = [];
          }
          groups[cart.storeName].push(cart);
        }
        return groups;
      }, {});
    },
    totalPrice() {
      if (!Array.isArray(this.carts)) {
        return 0;
      }
      return this.carts.reduce(
        (total, cart) => total + (cart.totalPrice || 0),
        0
      );
    },
    isReservationEnabled() {
      return this.checkedCarts.length > 0;
    },
    finalTotalPrice() {
      const discountedPrice = this.totalPrice - this.discountAmount;
      return Math.max(discountedPrice, 0);
    },
  },
  created() {
    this.fetchUserCart();
  },
  methods: {
    fetchUserCart() {
      axios
        .get("/api/cart")
        .then((response) => {
          this.carts = Array.isArray(response.data.data)
            ? response.data.data
            : [];
          if (this.carts.length > 0) {
            this.maxPeoplePerReserve = this.carts[0].maxPeoplePerReserve || 0;
            if (this.carts[0].availableCoupons) {
              this.globalCoupons = this.carts[0].availableCoupons;
            }
          }
        })
        .catch((error) => {
          if (error.response && error.response.status === 401) {
            alert("로그인이 필요한 서비스입니다.");
            window.location.href = "userLogin";
          } else if (error.response && error.response.status === 500) {
            alert("서버에 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
          } else {
            alert("데이터를 가져오는 중 오류가 발생했습니다.");
          }
        });
    },
    makeReservation() {
      const urlParams = new URLSearchParams(window.location.search);
      const reserveTime = urlParams.get("reserveTime");

      this.reservationNameError = "";
      this.peopleCountError = "";

      // 예약자명 검증
      if (!this.reservationName.trim()) {
        this.reservationNameError = "예약자명을 입력해 주세요.";
        this.$refs.reservationNameInput?.focus();
        return;
      }

      // 인원수 검증
      if (
        !Number.isInteger(this.peopleCount) ||
        this.peopleCount < 1 ||
        this.peopleCount > this.maxPeoplePerReserve
      ) {
        this.peopleCountError = `인원수는 1 이상, 최대 ${this.maxPeoplePerReserve}명까지 가능합니다.`;
        this.$refs.peopleCountInput?.focus();
        return;
      }

      const reservationData = {
        reserveTime,
        name: this.reservationName.trim(),
        people: this.peopleCount,
        memo: this.note.trim(),
      };

      if (this.selectedCoupon?.couponId) {
        reservationData.couponId = this.selectedCoupon.couponId;
      }

      axios
        .post("/api/reserve", reservationData, {
          headers: { "Content-Type": "application/json" },
        })
        .then(() => {
          alert("예약이 완료되었습니다!");
          window.location.href = "/";
        })
        .catch((error) => {
          const errorMessage =
            error.response?.data?.message ||
            "예약 중 알 수 없는 오류가 발생했습니다. 다시 시도해 주세요.";
          alert("오류 발생: " + errorMessage);
        });

      this.reservationName = "";
      this.peopleCount = 1;
      this.note = "";
    },
    getStoreImage(urlArray) {
      if (!Array.isArray(urlArray)) return null;
      return urlArray[0] || urlArray[1] || urlArray[2] || null;
    },
    async removeSelectedItems() {
      try {
        await Promise.all(
          this.checkedCarts.map((cart) =>
            axios.delete(`/api/cart/${cart.cartId}`)
          )
        );
        this.carts = this.carts.filter(
          (cart) => !this.checkedCarts.includes(cart)
        );
        this.checkedCarts = [];
        alert("선택된 항목이 삭제되었습니다.");
      } catch (error) {
        alert("삭제 중 오류가 발생했습니다.");
      }
    },
    openReservationModal() {
      this.isModalOpen = true;
    },
    closeReservationModal() {
      this.isModalOpen = false;
    },

    applyDiscount() {
      if (this.selectedCoupon && this.selectedCoupon.couponId !== null) {
        const discount = this.selectedCoupon.discount;
        this.discountAmount =
          discount < 100
            ? Math.round(this.totalPrice * (discount / 100))
            : discount;
      } else {
        this.discountAmount = 0;
      }
    },
    selectPayment(method) {
      this.selectedPayment = method;
    },
  },
});
