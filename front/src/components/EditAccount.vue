<template>
  <div class="container mt-4" style="max-width: 90%;">
    <h3 class="mb-3">管理介面</h3>
    <!-- 切換分頁 -->
    <div class="btn-group mb-3">
      <button class="btn"
        :class="{ 'btn-primary': currentTab === 'account', 'btn-outline-primary': currentTab !== 'account' }"
        @click="currentTab = 'account'">帳號管理</button>
      <button class="btn"
        :class="{ 'btn-primary': currentTab === 'group', 'btn-outline-primary': currentTab !== 'group' }"
        @click="currentTab = 'group'">組別列表</button>
      <!-- <button class="btn"
        :class="{ 'btn-primary': currentTab === 'rules', 'btn-outline-primary': currentTab !== 'rules' }"
        @click="currentTab = 'rules'">詞彙規則</button> -->

    </div>

    <!-- 帳號管理區塊 -->
    <div v-if="currentTab === 'account'">

      <!-- 密碼強度提示 -->
      <div class="alert alert-info" role="alert">
        🔐 設定密碼需符合以下規則：
        <ul class="mb-0">
          <li>長度至少 8 個字元</li>
          <li>需包含數字、大小寫英文字母</li>
          <li>且至少包含一個特殊符號（例如：!@#$%^&*~）</li>
        </ul>
      </div>

      <!-- 展開新增帳號按鈕 -->
      <button class="btn btn-primary mb-3" @click="toggleCreateForm">
        {{ showCreateForm ? '收起新增帳號' : '➕ 新增帳號' }}
      </button>

      <!-- 新增帳號表單 -->
      <div v-if="showCreateForm" class="card p-3 mb-4">
        <h5>新增帳號</h5>
        <div class="form-group mt-2">
          <label>帳號</label>
          <input v-model="username" type="text" class="form-control" placeholder="請輸入帳號">
        </div>
        <div class="form-group mt-2">
          <label>密碼</label>
          <input v-model="password" type="password" class="form-control" placeholder="請輸入密碼">
        </div>
        <div class="form-group mt-2">
          <label>角色</label>
          <select v-model="role" class="form-select">
            <option disabled value="">請選擇角色</option>
            <option value="admin">管理員</option>
            <option value="chief_censor">總審查</option>
            <option value="censor_team_leader">審查組長</option>
            <option value="committee">委員</option>
            <option value="special_professor">最高權限</option>
          </select>

          <!-- 額外選擇組別：只在角色為 審查組長 或 委員 時顯示 -->
          <div class="form-group mt-2" v-if="role === 'censor_team_leader' || role === 'committee'">
            <label>分組</label>
            <select v-model="selectedGroup" class="form-select">
              <option disabled value="">請選擇組別</option>
              <option v-for="group in groupList" :key="group.group_id" :value="group.group_id">
                {{ group.group_name }}
              </option>
            </select>
          </div>

        </div>
        <button class="btn btn-success mt-3" @click="createUser">送出</button>
        <p v-if="message" :style="{ color: isSuccess ? 'green' : 'red' }">{{ message }}</p>
      </div>

      <!-- 帳號列表 -->
      <ul class="list-group">
        <li class="list-group-item d-flex justify-content-between align-items-center" v-for="user in userList"
          :key="user.user_id">
          <!-- 左側 帳號與角色 與 建立時間 -->
          <div class="d-flex align-items-center flex-grow-1">
            <div v-if="editId !== user.user_id">
              <strong>{{ user.username }}</strong> ：
              <span>{{ getRoleDisplay(user.role) }}</span>
              <span v-if="user.role === 'committee' || user.role === 'censor_team_leader'">
                （組別：{{ getGroupNameById(user.group_id) || '未指定' }}）
              </span>
            </div>

            <div v-else>
              <input v-model="editStates[user.user_id].username" class="form-control mb-1" placeholder="帳號名稱"
                style="width: auto; display: inline-block; margin-right: 10px;">
              <input v-model="editStates[user.user_id].password" class="form-control mb-1" placeholder="新密碼（留空不變更）"
                type="password" style="width: auto; display: inline-block; margin-right: 10px;">

              <select v-model="editStates[user.user_id].role" class="form-select d-inline-block ms-2"
                style="width: auto;">
                <option value="admin">管理員</option>
                <option value="chief_censor">總審查</option>
                <option value="censor_team_leader">審查組長</option>
                <option value="committee">委員</option>
                <option value="special_professor">最高權限</option>
              </select>

              <!-- 顯示分組選單（僅在審查組長/委員角色時顯示） -->
              <select
                v-if="editStates[user.user_id].role === 'committee' || editStates[user.user_id].role === 'censor_team_leader'"
                v-model="editStates[user.user_id].group_id" class="form-select d-inline-block ms-2"
                style="width: auto;">
                <option disabled value="">請選擇組別</option>
                <option v-for="group in groupList" :key="group.group_id" :value="group.group_id">
                  {{ group.group_name }}
                </option>
              </select>

            </div>
            <small class="text-muted ms-auto me-3">建立時間：{{ formatDateTime(user.created_at) }}</small>
          </div>

          <!-- 右側 按鈕 -->
          <div class="d-flex gap-2">
            <div class="form-check form-switch">
              <input class="form-check-input" type="checkbox" :id="'toggle-' + user.user_id" :checked="user.enabled"
                @change="toggleUserEnabled(user.user_id)">
              <label class="form-check-label" :for="'toggle-' + user.user_id">
                {{ user.enabled ? '啟用中' : '已停用' }}
              </label>
            </div>


            <button @click="startEdit(user)" class="btn btn-sm btn-warning" v-if="editId !== user.user_id"
              :title="user.user_id <= 3 ? '此帳號為預設帳號，無法編輯' : ''">
              編輯
            </button>

            <button @click="confirmEdit(user)" class="btn btn-sm btn-success" v-if="editId === user.user_id">
              儲存
            </button>

            <button @click="cancelEdit" class="btn btn-sm btn-secondary" v-if="editId === user.user_id">
              取消
            </button>

            <button @click="deleteUser(user)" class="btn btn-sm btn-danger" :disabled="user.user_id <= 3"
              :title="user.user_id <= 3 ? '此帳號為預設帳號，無法刪除' : ''">
              刪除
            </button>
          </div>

        </li>
      </ul>
    </div>

    <!-- 組別管理區塊 -->
    <div v-if="currentTab === 'group'">
      <h5 class="mb-3">目前組別</h5>
      <ul class="list-group mb-3">
        <li class="list-group-item d-flex justify-content-between align-items-center"
          v-for="(group, index) in groupList" :key="group.group_id">
          <div>
            <strong>組別名稱：</strong>
            <span>{{ group.group_name }}</span>
          </div>

          <button class="btn btn-sm btn-danger" @click="deleteGroup(group)" :disabled="index !== groupList.length - 1"
            :title="index !== groupList.length - 1 ? '只能刪除最後一個組別' : ''">
            刪除
          </button>
        </li>
      </ul>

      <button class="btn btn-primary" @click="addGroup">➕ 新增下一個組別</button>
    </div>

    <!-- 詞彙規則區塊 -->
    <div v-if="currentTab === 'rules'">
      <h5 class="mb-3">詞彙規則</h5>
      <!-- 密碼強度提示 -->
      <div class="alert alert-info" role="alert">
        此詞彙規則使用時機：
        <ul class="mb-0">
          <li>新增、編輯詞彙時，需要連結未新增之詞彙</li>
        </ul>
      </div>
      <!-- 顯示每種類型的規則 -->

      <!-- 可編輯規則 -->
      <div v-for="(ruleGroup, type) in editableRules" :key="type" class="mb-4 p-3 border rounded bg-light">
        <h6>{{ resolveRelationType(type) }}</h6>

        <!-- 新增的開關按鈕 -->
        <div class="form-check form-switch mb-2">
          <input class="form-check-input" type="checkbox" :id="'toggle-' + type" v-model="ruleGroup.number" />
          <label class="form-check-label" :for="'toggle-' + type">
            <span v-if="ruleGroup.number">目前為：英文 + 數字編號（如 S1, S2...）</span>
            <span v-else>目前為：固定規則編號（如 a, b, c...）</span>
          </label>
        </div>

        <!-- 若此規則使用順序編號，顯示提示文字 -->
        <!-- <p v-if="ruleGroup.number" class="text-muted small mb-2">
          此規則為按照以下規則 (英文字) + 1, 2, 3... 的順序使用
        </p> -->

        <ul>
          <!-- 若為 number:true，只顯示第一個 -->
          <li v-if="ruleGroup.number" class="mb-2 d-flex align-items-center">
            <input v-model="ruleGroup.prefix" class="form-control form-control-sm w-auto me-2" placeholder="輸入代號" />
          </li>

          <!-- 若為 number:false，顯示所有 -->
          <li v-for="(rule, idx) in ruleGroup.rules" :key="idx" v-if="!ruleGroup.number"
            class="mb-2 d-flex align-items-center">
            <input v-model="ruleGroup.rules[idx]" class="form-control form-control-sm w-auto me-2" placeholder="輸入代號" />
            <button class="btn btn-danger btn-sm" @click="removeRule(type, idx)">刪除</button>
          </li>
        </ul>
        <button class="btn btn-primary btn-sm mt-2" @click="addRule(type)"
          :disabled="ruleGroup.number && ruleGroup.rules.length >= 1">
          新增規則
        </button>
        <button class="btn btn-success btn-sm mt-2 ms-2" @click="updateRelationRules">更新規則</button>
      </div>

    </div>
  </div>
  <div style="padding-bottom: 2%;"></div>
</template>


<script>
import instance from '@/utils/axios.js';

export default {
  data() {
    return {
      userList: [],
      editStates: {}, // 針對每個使用者 id 儲存 { username, password, role, isEditing }
      editId: null,
      editRole: '',
      editUsername: '',
      editPassword: '',
      username: '',
      password: '',
      role: '',
      message: '',
      isSuccess: false,
      showCreateForm: false,
      currentTab: 'account',  // 預設顯示帳號管理
      userList: [],
      groupList: [],          // 儲存組別資料
      selectedGroup: '', // 使用者選擇的組別（若需要）
      relationRules: {},
      editableRules: {},
    }
  },
  created() {
    this.fetchUsers();
    this.fetchGroups();
    this.fetchRelationRules();
  },
  methods: {
    async toggleUserEnabled(userId) {
      try {
        const res = await instance.post(`/user/toggle-enabled/${userId}`);

        if (res.data.success && res.data.updatedUser) {
          const updated = res.data.updatedUser;
          const index = this.userList.findIndex(u => u.user_id === userId);
          if (index !== -1) {
            this.userList.splice(index, 1, updated);
          }
        }
      } catch (err) {
        console.error(err);
        alert("操作失敗");
      }
    },
    getGroupNameById(groupId) {
      const group = this.groupList.find(g => g.group_id === groupId);
      return group ? group.group_name : null;
    },
    formatDateTime(isoString) {
      const date = new Date(isoString);
      const yyyy = date.getFullYear();
      const mm = String(date.getMonth() + 1).padStart(2, '0');
      const dd = String(date.getDate()).padStart(2, '0');
      const hh = String(date.getHours()).padStart(2, '0');
      const mi = String(date.getMinutes()).padStart(2, '0');
      return `${yyyy}-${mm}-${dd} ${hh}:${mi}`;
    },

    getRoleDisplay(role) {
      if (role === 'admin') return '管理員';
      if (role === 'chief_censor') return '總審查';
      if (role === 'censor_team_leader') return '審查組長';
      if (role === 'committee') return '委員';
      if (role === 'special_professor') return '最高權限';
      return role;
    },
    toggleCreateForm() {
      this.showCreateForm = !this.showCreateForm;
    },
    async fetchUsers() {
      try {
        const res = await instance.get('/users/fetch');
        console.log("載入帳號列表結果：", res.data);
        this.userList = res.data;
      } catch (error) {
        console.error("載入帳號列表失敗", error);
      }
    },

    startEdit(user) {
      this.editId = user.user_id; // <--- 設定 editId
      this.editStates[user.user_id] = {
        username: user.username,
        password: '',
        role: user.role,
        group_id: user.group_id || '', // 將 group_id 初始化（可為 null）
        isEditing: true
      };
    },

    cancelEdit(user) {
      delete this.editStates[user.user_id];
      this.editId = null; // <--- 清除 editId
    },
    confirmEdit(user) {
      const editState = this.editStates[user.user_id];

      console.log('填寫的密碼:', editState.password);
      // 密碼有填寫時，進行強度驗證
      if (editState.password && !this.isPasswordStrong(editState.password)) {
        alert('新密碼不符合規則');
        return;
      }
      // 準備 payload
      const payload = {
        user_id: user.user_id,
        username: editState.username,
        role: editState.role,
        group_id: editState.group_id
      };
      // 有填密碼才加進去
      if (editState.password) {
        payload.password = editState.password;
      }

      instance.post('/users/updateRole', payload)
        .then(() => {
          this.fetchUsers();
          this.cancelEdit(user);
          alert('更新成功');
        })
        .catch((error) => {
          const errorMsg = error.response?.data || '更新失敗';
          alert(errorMsg);
        });
    },
    deleteUser(user) {
      console.log('刪除帳號:', user);
      if (confirm(`確定要刪除帳號「${user.username}」嗎？`)) {
        instance.delete(`/users/delete/${user.user_id}`)
          .then(() => {
            this.fetchUsers();
            alert('刪除成功');
          }).catch(() => {
            alert('刪除失敗');
          });
      }
    },
    async createUser() {
      if (!this.username || !this.password || !this.role) {
        this.message = '請完整填寫所有欄位';
        this.isSuccess = false;
        return;
      }
      // 密碼強度驗證
      if (!this.isPasswordStrong(this.password)) {
        this.message = '密碼強度不符和要求：至少8個字元，包含數字、大小寫英文字母和特殊符號';
        this.isSuccess = false;
        return;
      }
      // 若是審查組長或委員，需選擇組別
      if ((this.role === 'censor_team_leader' || this.role === 'committee') && !this.selectedGroup) {
        this.message = '請選擇分組';
        this.isSuccess = false;
        return;
      }

      try {
        const res = await instance.post('/users/create', {
          username: this.username,
          password: this.password,
          role: this.role,
          group: this.selectedGroup
        });
        this.isSuccess = res.data.startsWith('新增成功');
        this.message = res.data;
        if (this.isSuccess) {
          this.username = '';
          this.password = '';
          this.role = '';
          this.selectedGroup = '';
          await this.fetchUsers();  // 重新載入帳號列表
        }
      } catch (error) {
        this.isSuccess = false;
        this.message = '請求失敗或伺服器錯誤';
      }
    },
    async fetchGroups() {
      try {
        const res = await instance.get('/groups');
        this.groupList = res.data;
      } catch (error) {
        console.error("取得組別失敗", error);
      }
    },
    async addGroup() {
      const nextChar = String.fromCharCode(65 + this.groupList.length); // A + n
      const exists = this.groupList.some(g => g.group_name === nextChar);
      if (exists) {
        alert('該組別已存在');
        return;
      }
      await instance.post('/groups/create', { group_name: nextChar });
      this.fetchGroups();
    },

    async deleteGroup(group) {
      if (group.group_name !== String.fromCharCode(65 + this.groupList.length - 1)) {
        alert('只能刪除最後一個組別，避免順序錯亂');
        return;
      }
      try {
        await instance.delete(`/groups/delete/${group.group_id}`);
        this.fetchGroups();
      } catch (error) {
        const msg = error.response?.data || '刪除失敗';
        alert(msg);
      }
    },
    // 密碼驗證
    isPasswordStrong(password) {
      const lengthCheck = password.length >= 8;
      const numberCheck = /[0-9]/.test(password);
      const lowerCheck = /[a-z]/.test(password);
      const upperCheck = /[A-Z]/.test(password);
      const specialCharCheck = /[!@#$%^&*~]/.test(password);
      return lengthCheck && numberCheck && lowerCheck && upperCheck && specialCharCheck;
    },
    resolveRelationType(type) {
      const map = {
        polysemy: '多義詞',
        synonym: '同義詞',
        antonym: '反義詞',
        related: '相關詞',
        variant: '變異詞'
      };
      return map[type] || type;
    },
    addRule(type) {
      this.editableRules[type].rules.push('');
    },
    removeRule(type, index) {
      this.editableRules[type].rules.splice(index, 1);
    },
    async updateRelationRules() {

      const payload = JSON.parse(JSON.stringify(this.editableRules));

      // 若為 number:true，只保留第一個 rule
      Object.keys(payload).forEach(type => {
        if (payload[type].number) {

          // 若 number 為 true：代表只用 prefix，需清空 rules
          const prefixRule = (payload[type].prefix) || '';
          payload[type].prefix = prefixRule.trim();  // 將第一個規則放入 prefix
          payload[type].rules = []; // 清空 rules
        }
        else {
          // 若 number 為 false：代表使用 rules，prefix 清空
          payload[type].prefix = '';
          payload[type].rules = (payload[type].rules || [])
            .map(rule => rule.trim())         // 去除空白
            .filter(rule => rule.length > 0); // 過濾空字串
        }
      });

      try {
        await instance.post('/rules/update', payload);
        alert('已更新成功！');
      } catch (e) {
        console.error(e);
        alert('更新失敗');
      }
    },
    async fetchRelationRules() {
      try {
        const res = await instance.get('/rules/fetch');
        this.relationRules = res.data;
        console.log("載入詞彙規則結果：", this.relationRules);

        // 初始化 editableRules
        this.editableRules = Object.keys(this.relationRules).reduce((acc, key) => {
          acc[key] = { ...this.relationRules[key] };
          return acc;
        }, {});
      } catch (error) {
        console.error("載入詞彙規則失敗", error);
      }
    },

  }
}
</script>
