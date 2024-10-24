
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

        getAllReviews(orderMethod){
            //쿼리 파라미터 설정
            const params = {sortMethod : orderMethod};
            axios.get("/api/owner/review", {params})
            .then((response =>{
               console.log(response); 
                this.totalReviewCount = response.data.data.totalReview;
                this.totalTodayReview = response.data.data.todayReview;
                this.totalAvgScore = this.totalReviewCount > 0 ? Math.round(((response.data.data.totalScore) / this.totalReviewCount) * 100) /100 : 0;
                this.totalAvgTodayScore = this.totalTodayReview > 0 ? Math.round(((response.data.data.todayTotalScore) / this.totalTodayReview) * 100) /100 : 0 ;
                this.getAllReviewList = response.data.data.reviews;
            }))
            .catch((error)=>{
                console.error(error);
            })
        },
    },
    mounted(){
        this.getAllReviews('score');
    }
})

