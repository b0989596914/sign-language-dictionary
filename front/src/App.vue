<template>
  <div style="font-family: Noto Sans;">
    <nav class="navbar" style="background-color: var(--white);margin-top:0.8%;">
      <router-link class="nav_p" to="/home">TSLDIC 詞彙管理系統</router-link>

      <!-- 新增詞彙 & 搜尋詞彙按鈕區 -->
      <div class="nav-tools ml-auto d-flex align-items-center gap-3" v-if="role !== '尚未登入' && this.$route.path !== '/'">
        <router-link to="/NewWord" class="btn btn-outline-dark btn-sm">新增詞彙</router-link>
        <router-link to="/EditCategory" class="btn btn-outline-dark btn-sm">管理分類</router-link>

        <div class="search-input-wrapper">
          <input type="text" v-model="searchKeyword" @keyup.enter="triggerSearch" placeholder="輸入關鍵字搜尋"
            class="form-control form-control-sm" />
        </div>

        <button class="btn btn-outline-dark btn-sm" @click="clearSearchKeyword()">清除關鍵詞</button>

        <router-link to="/EditAccount" class="btn btn-outline-dark btn-sm"
          v-if="resolveRoleName(role || '') === '管理員'">管理帳號</router-link>

        <router-link to="/reports" class="btn btn-outline-dark btn-sm"
          v-if="role === 'special_professor' || role === 'admin'">回報總覽</router-link>

        <router-link to="/AnnouncementManager" class="btn btn-outline-dark btn-sm"
          v-if="role === 'special_professor' || role === 'admin'">公告管理</router-link>
      </div>

      <div v-if="role !== '尚未登入' && this.$route.path !== '/'" class="role-container">
        <span>身份：{{ resolveRoleName(role) }}</span>
        <button @click="logout" class="btn btn-outline-dark btn-sm mx-4">登出</button>
      </div>
      <!-- <button 
          class="navbar-toggler" 
          type="button" 
          @click="toggleMenu"
          aria-controls="navbarScroll" 
          :aria-expanded="isMenuOpen" 
          aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button> -->

      <!-- <div class="collapse navbar-collapse" :class="{ 'show': isMenuOpen }" id="navbarScroll">
          <ul class="nav nav-pills mr-auto">
            <li class="nav-item">
              <button class="nav_list">
                <router-link to="/Signup" class="btn_color">登入</router-link>
              </button>
            </li>
          </ul>
        </div> -->
    </nav>

    <router-view :search-keyword="searchKeyword" />
  </div>
</template>

<script>
import instance from '@/utils/axios.js';
import { fetchSessionRole } from '@/utils/auth';

export default {
  data() {
    return {
      searchKeyword: this.$route.query.keyword || '', // 預設從網址取得
      role: '尚未登入',
      user_id: null,
    };
  },

  async mounted() {

    // if (this.$route.path !== '/') {
    this.refreshSession()
    // }
  },
  watch: {
    '$route.query.keyword'(newKeyword) {
      this.searchKeyword = newKeyword || '';
    },
    '$route.path'(newPath) {
      if (newPath === '/home') {
        this.refreshSession();
      }
    }
  },
  methods: {
    clearSearchKeyword() {
      this.searchKeyword = '';
      this.$router.push({ path: '/home' });  // 將網址參數清除
    },
    // toggleMenu() {
    //   const menu = document.getElementById("navbarScroll");
    //   if (menu) {
    //     menu.classList.toggle("show");
    //   }
    // },
    triggerSearch() {
      this.$emit('update:searchKeyword', this.searchKeyword);
      this.$router.push({ path: '/home', query: { keyword: this.searchKeyword } });
    },
    async refreshSession() {
      try {
        const sessionInfo = await fetchSessionRole();
        if (sessionInfo.role) {
          this.role = sessionInfo.role;
          this.user_id = sessionInfo.user_id;
          console.log('重新抓取 session:', this.role);
        }
      } catch (e) {
        console.warn('重新取得 session 失敗');
        this.role = '尚未登入';
      }
    },
    async logout() {
      try {
        const response = await instance.post('/Logout', {}, {
          withCredentials: true
        });

        this.role = '尚未登入'; // 更新畫面上的狀態
        this.$router.push('/');
      } catch (error) {
        console.error('登出失敗：', error);
      }
    },
    resolveRoleName(roleKey) {
      const roleMap = {
        admin: '管理員',
        chief_censor: '總審查',
        censor_team_leader: '審查組長',
        committee: '委員',
        special_professor: '最高權限',
      };
      return roleMap[roleKey] || '尚未登入';
    },
  },
};
</script>

<style>
:root {
  --main-textcolor: #18112e;
  --white: #ffffff;
}

html,
body {
  margin: 0;
  padding: 0;
  max-width: 100%;
  box-sizing: border-box;
}

.container-fluid {
  margin: 0;
}

nav a {
  font-size: 16px;
  text-decoration: none;
}

.nav {
  background-color: var(--white);
  display: flex;
  flex-direction: row;
}

.nav_p {
  color: var(--main-textcolor);
  font-size: 24px;
  margin-left: 3%;
}

.nav_list {
  background-color: var(--white);
  /* border-radius: 20px 20px 5px 5px; */
  border: none;
  border-bottom: var(--main-textcolor) 2px solid;
  margin: 10px;
}

.nav-item {
  padding: 0 10px;
}

.btn_color {
  color: var(--main-textcolor);
}

.stop_navbar {
  background-color: var(--white);
  width: 100%;
  /* height: 8%; */
  /* z-index: 2; */
  top: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.collapse {
  justify-content: right;
}

.collapse.show {
  display: flex;
  flex-direction: column;
  justify-content: right;
  padding: 10px 0;
}

.navbar-toggler {
  position: absolute;
  top: 15px;
  right: 15px;
  z-index: 999;
}

/* @media (min-width: 1300px) {
  .nav {
    display: flex;
  }

  .navbar-toggler {
    display: none;
  }
} */

.nav-tools a {
  font-size: 14px;
  padding: 4px 10px;
  text-decoration: none;
  white-space: nowrap;
}

.nav-tools {
  display: flex;
  align-items: center;
  gap: 10px;
}

.navbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
}

/* 角色區域正常排列，不用絕對定位 */
.role-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 小螢幕換行置中 */
@media (max-width: 1200px) {
  .nav {
    display: none;
    flex-direction: column;
  }

  .navbar {
    flex-direction: column;
    align-items: center;
  }

  .nav-tools {
    margin-top: 10px;
    justify-content: center;
    flex-wrap: wrap;
  }

  .role-container {
    margin-top: 10px;
  }

  .collapse.show .nav-tools {
    display: flex;
    flex-direction: column;
    /* align-items: flex-end; */
    margin-top: 10px;
  }
}
</style>
