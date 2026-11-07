<template>
  <div class="home">
    <MainHome :search-keyword="searchKeyword" />
    <!-- <button class="page-btn" @click="homeTo">Start to Upload ＞＞</button> -->
  </div>
</template>

<script>
// @ is an alias to /src
import MainHome from "@/components/mainHome.vue";

export default {
  name: "HomeView",
  props: {
    searchKeyword: {
      type: String,
      default: ''
    }
  },
  components: {
    MainHome,
  },
  methods: {
    homeTo() {
      this.$router.push("/stopAndChooseType");
    },
  },
};
</script>

<style>
h1 {
  font-size: 24px;
  color: #333;
}

p {
  font-size: 16px;
  color: #666;
}

.page-btn {
  background-color: #d3d1d1;
  margin: 20px;
}
</style>
