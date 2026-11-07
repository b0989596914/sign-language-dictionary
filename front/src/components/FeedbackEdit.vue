<template>
    <div class="container mt-4">

        <h5>詞彙資料</h5>
        <div v-if="word">
            <div class="card p-3 mb-4">
                <p><strong>編號：</strong>{{ word.word_id }}</p>
                <p><strong>詞彙：</strong>{{ word.word }}</p>
                <p><strong>分類：</strong>{{ word.categoryName || '（無）' }}</p>
                <p><strong>詞性：</strong>{{ word.word_type || '（無）' }}</p>
                <p><strong>手形：</strong>{{ word.handshape || '（無）' }}</p>
                <p><strong>用法：</strong>{{ word.useage || '（無）' }}</p>
                <div v-if="word.groupedRelations">
                    <div v-for="(items, type) in word.groupedRelations" :key="type"
                        class="mb-3 border rounded p-2 bg-light">
                        <p><strong>類型：</strong>{{ related_word_name(type) }}</p>
                        <ul>
                            <li v-for="(rel, idx) in items" :key="idx">
                                {{ rel.related_word_id }} {{ rel.related_word || '' }}
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="mb-3">
                    <label>詞彙回饋：</label>
                    <textarea v-model="fieldOne" class="form-control" rows="3" placeholder="輸入詞彙的回饋內容"></textarea>
                </div>
            </div>

            <!-- 顯示例句 -->
            <div class="card p-3 mb-4" v-if="word.examples?.length">
                <h5>例句列表</h5>
                <div v-for="(ex, idx) in word.examples" :key="idx" class="mb-3 border rounded p-2 bg-light">
                    <p><strong>例句編號：</strong>{{ ex.example_index }}</p>
                    <p><strong>手語：</strong>{{ ex.sign }}</p>
                    <p><strong>中文：</strong>{{ ex.text }}</p>

                    <div class="mb-3" v-if="ex.example_index === 1">
                        <label>例句一回饋：</label>
                        <textarea v-model="fieldTwo" class="form-control" rows="3" placeholder="輸入例句一的回饋內容"></textarea>
                    </div>
                    <div class="mb-3" v-if="ex.example_index === 2">
                        <label>例句二回饋：</label>
                        <textarea v-model="fieldThree" class="form-control" rows="3" placeholder="輸入例句二的回饋內容"></textarea>
                    </div>
                </div>                
            </div>  
            <div class="text-end">
                <button class="btn btn-primary" @click="submitFeedback">提交回饋</button>
            </div>          
        </div>
        <div v-else>
            <p>載入中...</p>
        </div>
    </div>
</template>

<script>
import instance from '@/utils/axios';

export default {
    data() {
        return {
            wordId: '',
            word_gadd_person_group_id: '',
            fieldOne: '',
            fieldTwo: '',
            fieldThree: '',
            word: null,        // 從後端取得的詞彙資料
        };
    },
    async created() {
        this.wordId = this.$route.params.word_id;
        try {
            const res = await instance.get(`/feedbackWords/${this.wordId}`);
            console.log('📦 從後端取得的資料：', res.data);
            this.word_gadd_person_group_id = res.data.added_person_group_id;
            this.word = res.data;
        } catch (error) {
            alert('無法取得詞彙資料，請回首頁');
            this.$router.replace('/home');
        }
    },
    methods: {
        related_word_name(type) {
            const map = {
                polysemy: '多義詞',
                synonym: '同義詞',
                antonym: '反義詞',
                related: '關聯詞',
                variant: '變異詞',
            };

            return map[type] || type || '';
        },
        async submitFeedback() {
            try {
            const payload = {
                word_id: this.wordId,
                word_group_id: this.word_gadd_person_group_id,
                feedback_word: this.fieldOne,
                feedback_example1: this.fieldTwo,
                feedback_example2: this.fieldThree,
            };
            console.log(this.word);
            await instance.post('/submitFeedback', payload);
            alert('回饋送出成功！');
            this.$router.push('/home'); // 或其他頁面
            } catch (err) {
            alert('送出失敗');
            console.error(err);
            }
        },
    },
};
</script>

<style scoped>
.container {
    max-width: 600px;
    margin: 0 auto;
}

.card {
    background-color: #f9f9f9;
}

.mb-3 {
    margin-bottom: 1rem;
}
</style>
