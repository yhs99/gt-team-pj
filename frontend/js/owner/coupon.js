new Vue({
    el: "#app",
    data: {
        coupons: [],
        selectedCouponId: null,
        couponCreationType: null, // 쿠폰 생성 방식 (date / stock)
        newCoupon: {
            couponName: '',
            discount: null,
            stock: 0,
            start: '', // 빈 문자열로 초기화
            end: '',   // 빈 문자열로 초기화
        },
        originalCoupon: null, // 원래 쿠폰 정보를 저장할 변수 추가
        currentPage: 1, // 현재 페이지
        pageSize: 5, // 페이지당 표시 갯수
        couponCount: 0, // 검색 총 결과수
        totalPages: 0, // 총 페이지
        paginatedCoupons: [] // 페이지네이션된 쿠폰 리스트
    },
    computed: {
        formattedDiscount() {
            return this.newCoupon.discount > 100 ? `${this.newCoupon.discount} 원` : `${this.newCoupon.discount} %`;
        },
        validCoupons() {
            return this.coupons.filter(coupon => !coupon.isDeleted);
        },
        formattedCoupons() {
            return this.validCoupons.map(coupon => {
                return {
                    ...coupon,
                    formattedStart: coupon.start ? this.formatDate(coupon.start) : null,
                    formattedEnd: coupon.end ? this.formatDate(coupon.end) : null,
                };
            });
        }
    },
    created() {
        this.getAllCoupons(); // 첫 번째 페이지 데이터 로드
    },
    mounted() {
        this.paginateCoupons(); // 첫 번째 페이지 로드 시 페이지네이션 처리
    },
    watch: {
        // currentPage가 변경될 때마다 페이지네이션 처리
        currentPage() {
            this.paginateCoupons();
        }
    },
    methods: {
        paginateCoupons() {
            const startIndex = (this.currentPage - 1) * this.pageSize;
            const endIndex = startIndex + this.pageSize;
            this.paginatedCoupons = this.formattedCoupons.slice(startIndex, endIndex); // 현재 페이지에 해당하는 데이터만 슬라이싱
        },
        
        changePage(page) {
            if (page >= 1 && page <= this.totalPages) {
                this.currentPage = page;
                this.paginateCoupons();  // 페이지 변경 시 페이지네이션 실행
            }
        },
        
        getAllCoupons() {
            axios.get("/api/coupon")
                .then((response) => {
                    this.coupons = Array.isArray(response.data.data) ? response.data.data : [];
                    this.couponCount = response.data.data.length;
                    this.totalPages = Math.ceil(this.couponCount / this.pageSize);
                    this.paginateCoupons(); // 페이지네이션 호출
                })
                .catch((error) => {
                    console.error('Error fetching coupons:', error);
                });
        },
        
        formatDate(dateArray) {
            if (!dateArray || !Array.isArray(dateArray)) return '';
            const year = dateArray[0];
            const month = String(dateArray[1]).padStart(2, '0');
            const day = String(dateArray[2]).padStart(2, '0');
            return `${year}-${month}-${day}`; // "2024-11-04" 형태로 변환
        },
        
        startEdit(couponId) {
            const couponToEdit = this.formattedCoupons.find(coupon => coupon.couponId === couponId);
            if (couponToEdit) {
                this.selectedCouponId = couponId;
                this.originalCoupon = { ...couponToEdit }; // 원래 쿠폰 정보 저장
                // 쿠폰의 기존 데이터를 newCoupon으로 복사
                this.newCoupon = {
                    couponName: couponToEdit.couponName,
                    discount: couponToEdit.discount,
                    stock: couponToEdit.stock,
                    start: this.formatDate(couponToEdit.start), // 형식 변환
                    end: this.formatDate(couponToEdit.end), // 형식 변환
                };
            }
        },
        
        isEditing(couponId) {
            return this.selectedCouponId === couponId;
        },
        
        createCoupon() {
            const formData = this.prepareCouponData(false, this.couponCreationType === 'stock'); // 쿠폰 데이터 준비
            if (!formData) return; // 유효성 검사 통과하지 못한 경우 종료
        
            // 생성 요청
            axios.post('/api/coupon', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
            .then(response => {
                this.getAllCoupons(); // 쿠폰 목록 새로 고침
                this.resetForm();
            })
            .catch(error => {
                console.error('Error creating coupon:', error.response ? error.response.data : error.message);
            });
        },
        
        updateCoupon() {
            const coupon = this.coupons.find(c => c.couponId === this.selectedCouponId);
            if (!coupon) return; // 쿠폰이 없으면 종료

            const isStockCoupon = coupon.stock > 0; // 현재 쿠폰의 creationType 확인

            const formData = this.prepareCouponData(true, isStockCoupon, coupon); // coupon을 매개변수로 전달
            if (!formData) return; // 유효성 검사 통과하지 못한 경우 종료

            // 업데이트 요청
            axios.put(`/api/coupon/${this.selectedCouponId}`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
            .then(response => {
                console.log('Coupon updated:', response.data);
                this.getAllCoupons(); // 쿠폰 목록 새로 고침
                this.resetForm();
            })
            .catch(error => {
                console.error('Error updating coupon:', error.response ? error.response.data : error.message);
            });
        },
        
        prepareCouponData(isUpdate = false, isStockCoupon = false, coupon = null) {
            const formData = new FormData();
            formData.append('couponName', this.newCoupon.couponName);
            formData.append('discount', this.newCoupon.discount);
        
            if (isStockCoupon) {
                // 수량 쿠폰일 경우
                formData.append('stock', this.newCoupon.stock || 0); // 0 또는 입력한 값
                formData.append('start', ''); // DB에 null로 저장
                formData.append('end', ''); // DB에 null로 저장
            } else {
                // 날짜 쿠폰일 경우
                if (!this.newCoupon.start || !this.newCoupon.end) {
                    alert("시작일과 종료일을 모두 입력해야 합니다.");
                    return null; // 유효성 검사 통과하지 못한 경우
                }
                formData.append('stock', 0); // stock은 0으로 설정
                formData.append('start', this.newCoupon.start); // 시작일
                formData.append('end', this.newCoupon.end); // 종료일
            }
        
            // 수정 시 기존 값 유지
            if (isUpdate && coupon) {
                formData.append('couponId', this.selectedCouponId); // 업데이트 시 couponId 추가
                if (isStockCoupon) {
                    formData.append('stock', this.newCoupon.stock || coupon.stock); // 기존 stock 값 사용
                    formData.append('start', ''); // 수량 쿠폰일 때는 날짜는 null로
                    formData.append('end', ''); // 수량 쿠폰일 때는 날짜는 null로
                } else {
                    formData.append('start', this.newCoupon.start || coupon.start);
                    formData.append('end', this.newCoupon.end || coupon.end);
                }
            }
        
            return formData;
        },
        
        deleteCoupon(couponId) {
            if (confirm("정말로 삭제하시겠습니까?")) {
                axios.delete(`/api/coupon/${couponId}`)
                    .then(response => {
                        this.getAllCoupons(); // 쿠폰 목록 새로 고침
                    })
                    .catch((error) => {
                        console.error('Error deleting coupon:', error);
                    });
            }
        },
        
        resetForm() {
            this.selectedCouponId = null;
            this.couponCreationType = null; // 생성 방식 초기화
            this.newCoupon = {
                couponName: '',
                discount: null,
                stock: 0,
                start: '',
                end: '',
            };
        }
    }
});
