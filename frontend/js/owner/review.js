
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
        getAllReviews(){
            axios.get("http://localhost/api/review")
            .then((response =>{
               console.log(response); 
            }))
            .catch((error)=>{
                console.error(error);
            })
        },
        getTotalReviewCount(){
            axios.get("http://localhost/api/review/info")
            .then((response) =>{
                console.log(response)
                this.totalReviewCount = response.data.data.totalReview;
                this.totalTodayReview = response.data.data.todayReview;
                this.totalAvgScore = Math.round(((response.data.data.totalScore) / this.totalReviewCount) * 100) /100;
                this.totalAvgTodayScore = Math.round(((response.data.data.todayTotalScore) / this.totalTodayReview) * 100) /100;
            })
            .catch((error) =>{
                console.error(error);
            })
        }
    },
    mounted(){
        this.getAllReviews();
        this.getTotalReviewCount();
    }
})