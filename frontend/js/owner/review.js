
new Vue({
    el : '#vueApp',
    data : {
        getAllReviewList : [],
        totalReviewCount : 0,
        totalAvgScore : 0,
        totalTodayReview : 0,
        totalAvgTodayScore : 0
    },

    methods : {
        saveStoreDTO(){
            const storeDTO = {
                storeId: 3,
                ownerId: 101,
                rotationId: 5,
                sidoCodeId: 32,
                address: "123 Main St, City, Country",
                tel: "123-456-7890",
                description: "A great place to eat.",
                directionGuide: "Next to the big mall.",
                maxPeople: 50,
                maxPeoplePerReserve: 10,
                locationLatX: 37.123456,
                locationLonY: 127.123456
            };

            // sessionStorage.setItem('store',JSON.stringify(storeDTO));

            // const storedStoreDTO = JSON.parse(sessionStorage.getItem('store'));
           return  axios.post("/api/owner/review", storeDTO)
            .then(response =>{
                console.log('storeDTO saved successfully : ', response.data)
            })
            .catch(error =>{
                console.error('Error :' , error);
            });
        },

        getAllReviews(){
            axios.get("/api/owner/review")
            .then((response =>{
               console.log(response); 
                this.totalReviewCount = response.data.data.totalReview;
                this.totalTodayReview = response.data.data.todayReview;
                this.totalAvgScore = this.totalReviewCount > 0 ? Math.round(((response.data.data.totalScore) / this.totalReviewCount) * 100) /100 : 0;
                this.totalAvgTodayScore = this.totalTodayReview > 0 ? Math.round(((response.data.data.todayTotalScore) / this.totalTodayReview) * 100) /100 : 0 ;
            }))
            .catch((error)=>{
                console.error(error);
            })
        },
    },
    mounted(){
        this.saveStoreDTO().then(() => {
            this.getAllReviews();
        });
    }
})

