<template>
    <div class="container mt-4">
        <div class="card p-4 shadow-sm">
            <h2>詞彙詳情</h2>
            <p v-if="error" class="error">錯誤: {{ error }}</p>

            <div v-if="wordInfo">
                <p><strong>詞彙 ID：</strong> {{ wordInfo.word_id }}</p>
                <p><strong>詞彙名稱：</strong> {{ wordInfo.word }}</p>

                <label for="newOwner"><strong>選擇新擁有者：</strong></label>
                <select v-model="selectedOwnerId" class="form-select mt-1" id="newOwner">
                    <option disabled value="">請選擇新擁有者</option>
                    <option v-for="user in allUsers" :key="user.user_id" :value="user.user_id">
                        {{ user.username }}（{{ resolveRoleName(user.role) }}，群組：{{ user.group_name ?? '無' }}）
                    </option>
                </select>
                <button class="btn btn-success mt-3" @click="submitChange">變更擁有者</button>
            </div>
            <p v-else-if="!error">載入中...</p>
        </div>
    </div>
</template>

<script>

import instance from '@/utils/axios.js';

export default {
    data() {
        return {
            word_id: null, // 用來儲存從路由參數中獲取的詞彙 ID
            error: null,   // 用來儲存錯誤訊息
            wordInfo: null,
            allUsers: [],
            selectedOwnerId: '',  // 綁定下拉選單選擇

        };
    },
    created() {
        this.word_id = this.$route.params.word_id || null;
        this.fetchWord();
        // console.log('created:', this.word_id);
    },
    methods: {
        async fetchWord() {
            try {
                const response = await instance.get(`/changeOwner/${this.word_id}`);
                this.wordInfo = response.data.wordInfo;
                this.allUsers = response.data.allUsers;
                // console.log('獲取wordInfo資料:', this.wordInfo);
                // console.log('獲取allUsers資料:', this.allUsers);
                this.selectedOwnerId = this.wordInfo.added_person; // 預設為原擁有者
            } catch (error) {
                this.error = error.message;
                console.error('獲取詞彙失敗:', error);
            }
        },
        resolveRoleName(roleKey) {
            const roleMap = {
                admin: '管理員',
                chief_censor: '總審查',
                censor_team_leader: '審查組長',
                committee: '委員',
                //user: '一般使用者',
                special_professor: '最高權限',
            };
            return roleMap[roleKey] || '尚未登入';
        },
        async submitChange() {
            if (this.selectedOwnerId === this.wordInfo.added_person) {
                alert('請選擇新的擁有者');
                return;
            }
            else {
                try {
                    await instance.post('/changeOwner', {
                        wordId: this.wordInfo.word_id,
                        newOwnerId: this.selectedOwnerId
                    });
                    alert('變更成功！');
                } catch (err) {
                    alert('變更失敗');
                }
            }
        }
    }
}
</script>