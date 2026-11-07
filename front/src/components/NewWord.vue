<template>
    <div class="container mt-4">
        <h3 class="mb-3">新增詞彙</h3>

        <form @submit.prevent="submitCreate">
            <div class="form-group" v-for="field in fields" :key="field.key">
                <label :for="field.key">{{ field.label }}</label>

                <template v-if="field.key === 'category'">
                    <!-- 分類欄位下拉選單 -->
                    <multiselect v-model="formData.category" :options="categoryOptions" :multiple="true"
                        placeholder="請選擇分類(可多選)" label="label" track-by="value" class="form-control" />
                </template>

                <template v-else-if="field.key === 'word_type'">
                    <!-- 詞性下拉式選單 -->
                    <select class="form-select" v-model="formData.word_type" :id="field.key">
                        <option disabled value="">請選擇詞性</option>
                        <option v-for="option in wordTypeOptions" :key="option.value" :value="option.value">
                            {{ option.label }}
                        </option>
                    </select>
                    <!-- 當選到動詞時，顯示動詞類型+動詞屬性 -->
                    <div v-if="formData.word_type === '動詞'" class="mt-2">
                        <!--動詞屬性-->
                        <div class="mt-3">
                            <label>動詞屬性：</label>
                            <multiselect v-model="formData.verb_attributes" :options="verb_attributes" :multiple="true"
                                placeholder="請選擇動詞屬性（可多選）" label="label" track-by="value" class="form-control" />
                        </div>

                        <!--動詞類型-->
                        <label for="verb_type">動詞類型</label>
                        <select class="form-select" v-model="formData.verb_type" id="verb_type">
                            <option disabled value="">請選擇動詞類型</option>
                            <option v-for="option in verbSubtypes" :key="option.value" :value="option.value">
                                {{ option.label }}
                            </option>
                        </select>
                    </div>
                </template>

                <template v-else-if="multiSelectFields.includes(field.key)">
                    <multiselect v-model="formData[field.key]" :options="wordOptions" :multiple="true"
                        :close-on-select="false" :clear-on-select="false" :preserve-search="true" placeholder="請選擇"
                        label="label" track-by="value" class="form-control" />

                    <div v-for="(entry, index) in manualWords[field.key]" :key="index"
                        class="border p-2 my-2 rounded bg-light">
                        <div class="d-flex justify-content-between align-items-center">
                            <strong>新增詞彙 #{{ index + 1 }} </strong>

                            <div>
                                <button type="button" class="btn btn-sm btn-outline-secondary me-2"
                                    @click="toggleExpanded(field.key, index)">
                                    {{ entry.expanded ? '收合' : '展開' }}
                                </button>
                                <button type="button" class="btn btn-sm btn-outline-danger"
                                    @click="removeManualWord(field.key, index)">
                                    刪除
                                </button>
                            </div>
                        </div>

                        <!-- 內容收合區域 -->
                        <div v-show="entry.expanded" class="mt-2">
                            <!-- 詞彙名稱 -->
                            <input type="text" class="form-control mt-2" placeholder="請輸入新詞彙名稱" v-model="entry.name" />

                            <!-- 分類下拉 -->
                            <multiselect v-model="entry.category" :options="categoryOptions" :multiple="true"
                                label="label" track-by="value" placeholder="請選擇分類" class="form-control mt-2" />
                        </div>
                    </div>

                    <!-- 新增按鈕 -->
                    <button type="button" class="btn btn-sm btn-outline-primary mt-2" @click="addManualWord(field.key)"
                        :disabled="!canAddMore(field.key)">
                        新增詞彙
                    </button>

                </template>

                <!-- 一般欄位 -->
                <template v-else-if="!field.multiline">
                    <input type="text" class="form-control" :id="field.key" v-model="formData[field.key]"
                        :required="['word_id', 'word'].includes(field.key)" />
                </template>
                <template v-else>
                    <textarea class="form-control" :id="field.key" v-model="formData[field.key]" rows="2"></textarea>
                </template>
                <!-- 動態插入影片上傳區 -->

                <div v-if="field.key === 'useage'" class="mt-2">
                    <label for="word_video">詞彙影片</label>
                    <input type="file" class="form-control" id="word_video"
                        @change="handleFileChange($event, 'word_video')" accept=".mp4, .mov" />
                </div>
                <div v-else-if="field.key === 'example_text1'" class="mt-2">
                    <label for="example_video1">例句影片1</label>
                    <input type="file" class="form-control" id="example_video1"
                        @change="handleFileChange($event, 'example_video1')" accept=".mp4, .mov" />
                </div>
                <div v-else-if="field.key === 'example_text2'" class="mt-2">
                    <label for="example_video2">例句影片2</label>
                    <input type="file" class="form-control" id="example_video2"
                        @change="handleFileChange($event, 'example_video2')" accept=".mp4, .mov" />
                </div>
                <div v-else-if="field.key === 'example_text3'" class="mt-2">
                    <label for="example_video3">例句影片3</label>
                    <input type="file" class="form-control" id="example_video3"
                        @change="handleFileChange($event, 'example_video3')" accept=".mp4, .mov" />
                </div>
                <div v-else-if="field.key === 'example_text4'" class="mt-2">
                    <label for="example_video4">例句影片4</label>
                    <input type="file" class="form-control" id="example_video4"
                        @change="handleFileChange($event, 'example_video4')" accept=".mp4, .mov" />
                </div>
            </div>
            <div v-if="uploading" class="progress my-2">

                <label>上傳中，請稍候...</label>
                <div class="progress-bar" role="progressbar" :style="{ width: uploadProgress + '%' }">
                    {{ uploadProgress.toFixed(0) }}%
                </div>
            </div>
            <button type="submit" class="btn btn-primary mt-3">新增</button>
            <router-link to="/home" class="btn btn-link">返回列表</router-link>
        </form>
        </br>
    </div>
</template>

<script>
import instance from '@/utils/axios.js';
import Multiselect from 'vue-multiselect';
import 'vue-multiselect/dist/vue-multiselect.min.css';

export default {
    components: {
        Multiselect
    },
    data() {
        return {
            // 上傳進度條
            uploadProgress: 0,
            uploading: false,

            formData: {
                word_id: '',
                word: '',
                category: '',
                word_type: '',
                verb_type: '', // 當選擇「動詞」時才會填的子類型
                verb_attributes: [], // 當選擇「動詞」時才會填的子類型
                handshape: '',
                useage: '',
                polysemy: [],
                synonym: [],
                antonym: [],
                related: [],
                variant: [],
                example_handshape1: '',
                example_text1: '',
                note1: '',
                example_handshape2: '',
                example_text2: '',
                note2: '',
                example_handshape3: '',
                example_text3: '',
                note3: '',
                example_handshape4: '',
                example_text4: '',
                note4: '',
            },
            fields: [
                { key: 'word_id', label: '詞彙編號(必填)' },
                { key: 'word', label: '詞彙(必填)' },
                { key: 'category', label: '分類(必填)' },
                { key: 'word_type', label: '詞性' },
                { key: 'handshape', label: '手形' },
                { key: 'useage', label: '用法' },
                { key: 'polysemy', label: '多義詞', multiline: true },
                { key: 'synonym', label: '同義詞', multiline: true },
                { key: 'antonym', label: '反義詞', multiline: true },
                { key: 'related', label: '關聯詞', multiline: true },
                { key: 'variant', label: '變異詞', multiline: true },
                { key: 'example_handshape1', label: '例句-手語1' },
                { key: 'example_text1', label: '例句-中文1' },
                { key: 'note1', label: '例句1-備註', multiline: true },
                { key: 'example_handshape2', label: '例句-手語2' },
                { key: 'example_text2', label: '例句-中文2' },
                { key: 'note2', label: '例句2-備註', multiline: true },
                { key: 'example_handshape3', label: '例句-手語3' },
                { key: 'example_text3', label: '例句-中文3' },
                { key: 'note3', label: '例句3-備註', multiline: true },
                { key: 'example_handshape4', label: '例句-手語4' },
                { key: 'example_text4', label: '例句-中文4' },
                { key: 'note4', label: '例句4-備註', multiline: true },
            ],
            multiSelectFields: ['polysemy', 'synonym', 'antonym', 'related', 'variant'],
            wordOptions: [],
            videoFiles: {
                word_video: null,
                example_video1: null,
                example_video2: null,
                example_video3: null,
                example_video4: null,
            },
            categoryOptions: [],
            manualWords: {
                polysemy: [], synonym: [], antonym: [], related: [], variant: []
            },
            // 詞性選項
            wordTypeOptions: [
                { value: '名詞', label: '名詞' },
                { value: '動詞', label: '動詞' },
                { value: '形容詞', label: '形容詞' },
                { value: '副詞', label: '副詞' },
                { value: '疑問詞', label: '疑問詞' },
                { value: '代名詞', label: '代名詞' },
                { value: '指示詞', label: '指示詞' },
                { value: '指示代名詞', label: '指示代名詞' },
                { value: '助動詞', label: '助動詞' },
                { value: '數詞', label: '數詞' },
                { value: '時貌標記', label: '時貌標記' },
                { value: '連接詞', label: '連接詞' },
                { value: '感嘆詞', label: '感嘆詞' },
                { value: '語助詞', label: '語助詞' },
                { value: '其他', label: '其他' }
            ],
            verbSubtypes: [
                { value: '普通動詞', label: '普通動詞' },
                { value: '呼應動詞', label: '呼應動詞' },
                { value: '空間動詞', label: '空間動詞' }
            ],
            verb_attributes: [
                { value: '及物', label: '及物' },
                { value: '不及物', label: '不及物' },
                { value: '雙賓', label: '雙賓' },
                { value: '待確認', label: '( )' }
            ],
        };
    },
    created() {
        // 取得詞彙選項
        instance.get('/Create/option').then((res) => {
            const list = Array.isArray(res.data) ? res.data : res.data.data || [];
            this.wordOptions = list.map((item) => ({
                value: item.word_id,
                label: `${item.word_id} - ${item.word}`,
            }));
            // console.log("詞彙選項：", this.wordOptions);
        });
        // 取得分類選項（從 wordcategory 資料表）
        instance.get('/Create/categories').then((res) => {
            this.categoryOptions = res.data.map(cat => ({
                value: cat.id,
                label: cat.categoryName
            }));
        });
    },
    methods: {
        handleFileChange(event, key) {
            const file = event.target.files[0];
            if (!file) return;

            // 限制影片大小為200MB
            const maxSizeMB = 200;
            const maxSizeBytes = maxSizeMB * 1024 * 1024;
            // console.log("上傳的檔案大小：", file.size / 1024 / 1024);

            if (file.size > maxSizeBytes) {
                alert(`上傳失敗：影片大小不能超過 ${maxSizeMB}MB。\n目前選取的檔案大小為 ${(file.size / 1024 / 1024).toFixed(2)}MB`);
                event.target.value = '';  // 清空檔案欄位
                return;
            }

            this.videoFiles[key] = file;
        },
        async submitCreate() {
            const idRule = /^[a-zA-Z0-9_-]+$/;

            if (!idRule.test(this.formData.word_id)) {
                alert("詞彙編號格式錯誤：不可包含空格或特殊符號（僅限英數字、底線、橫線）");
                return;
            }
            try {
                // --- 準備例句資料陣列 ---
                const examples = [
                    { id: 1, sign: this.formData.example_handshape1, text: this.formData.example_text1 },
                    { id: 2, sign: this.formData.example_handshape2, text: this.formData.example_text2 },
                    { id: 3, sign: this.formData.example_handshape3, text: this.formData.example_text3 },
                    { id: 4, sign: this.formData.example_handshape4, text: this.formData.example_text4 }
                ];

                // --- 防呆：單句完整性檢查 ---
                // 檢查每一句：如果有填手語，就必須填中文
                for (let i = 0; i < examples.length; i++) {
                    const ex = examples[i];
                    const hasSign = ex.sign && ex.sign.trim();
                    const hasText = ex.text && ex.text.trim();

                    // 狀況 A：填了手語，沒填中文
                    if (hasSign && !hasText) {
                        alert(`資料不完整：\n您已填寫「例句 ${i + 1}」的手語，但未填寫中文。\n請補上「例句 ${i + 1} - 中文」。`);
                        return;
                    }

                    // 狀況 B：填了中文，沒填手語 
                    if (!hasSign && hasText) {
                        alert(`資料不完整：\n您已填寫「例句 ${i + 1}」的中文，但未填寫手語。\n請補上「例句 ${i + 1} - 手語」。`);
                        return;
                    }
                }

                // --- 防呆：填寫順序檢查 (1->2->3->4) ---
                const isExEmpty = (ex) => (!ex.sign || !ex.sign.trim()) && (!ex.text || !ex.text.trim());
                const firstEmptyIndex = examples.findIndex(ex => isExEmpty(ex));
                if (firstEmptyIndex !== -1) {
                    for (let i = firstEmptyIndex + 1; i < examples.length; i++) {
                        if (!isExEmpty(examples[i])) {
                            alert(`填寫順序錯誤：\n您在「例句 ${firstEmptyIndex + 1}」尚未填寫的情況下，填寫了「例句 ${i + 1}」。\n請依序填寫。`);
                            return;
                        }
                    }
                }

                // 檢查 word_id 是否已存在
                const checkRes = await instance.get('/Create/checkWordId', {
                    params: { word_id: this.formData.word_id }
                });

                if (checkRes.data === true) {
                    alert(`詞彙編號 ${this.formData.word_id} 已存在，請重新輸入`);
                    return;
                }
                // 檢查不同關聯詞是否選到相同的詞彙 --> 若有,則alert提示
                const allRelations = [
                    ...this.formData.polysemy,
                    ...this.formData.synonym,
                    ...this.formData.antonym,
                    ...this.formData.related,
                    ...this.formData.variant
                ].map(item => typeof item === 'object' ? item.value : item);

                // 檢查是否有重複的 word_id
                const duplicates = allRelations.filter((id, index, self) => self.indexOf(id) !== index);
                if (duplicates.length > 0) {
                    alert(`以下詞彙 ID 重複出現在多個關聯欄位中：\n${[...new Set(duplicates)].join(', ')}\n請檢查並重新輸入`);
                    return;
                }

                //選詞屬性，類別為必填
                const wordTypeVal = typeof this.formData.word_type === 'object'
                    ? this.formData.word_type.value
                    : this.formData.word_type;

                if (wordTypeVal === '動詞') {
                    if (!this.formData.verb_attributes || this.formData.verb_attributes.length === 0) {
                        alert("請選擇至少一個動詞屬性");
                        return;
                    }
                    if (!this.formData.verb_type) {
                        alert("請選擇動詞類型");
                        return;
                    }
                }

                // 顯示輸入資料
                // console.log("使用者輸入資料如下：");
                // for (const key in this.formData) {
                //     console.log(`${key}:`, this.formData[key]);
                // }

                // 原本的送出邏輯
                const form = new FormData();

                for (const key in this.formData) {
                    const value = this.formData[key];

                    // 處理多選欄位（陣列）
                    if (Array.isArray(value)) {
                        const values = value.map(v => typeof v === 'object' && v !== null && 'value' in v ? v.value : v);
                        form.append(key, values.join(','));
                    }
                    // 處理物件欄位（通常是下拉選單）
                    else if (value !== null && typeof value === 'object' && 'value' in value) {
                        form.append(key, value.value);
                    }
                    // 處理基本欄位
                    else {
                        form.append(key, value ?? ''); // 用 null 合併運算，避免 undefined 被忽略
                    }
                }

                // 新增未添加過的關聯詞彙 -> 需利用規則
                const manualWordsPayload = {};
                for (const type in this.manualWords) {
                    // 驗證：若填了名稱但沒選分類，則跳警告
                    const entries = this.manualWords[type];
                    const validEntries = [];

                    for (let i = 0; i < entries.length; i++) {
                        const entry = entries[i];
                        const hasName = entry.name.trim() !== '';
                        const hasCategory = Array.isArray(entry.category) && entry.category.length > 0;

                        if (hasName && !hasCategory) {
                            alert(`「${this.fields.find(f => f.key === type)?.label || type}」的第 ${i + 1} 筆：已填寫名稱但未選擇分類，請補上分類或清除名稱。`);
                            return;
                        }
                        if (!hasName && hasCategory) {
                            alert(`「${this.fields.find(f => f.key === type)?.label || type}」的第 ${i + 1} 筆：已選擇分類但未填寫名稱，請補上名稱或清除分類。`);
                            return;
                        }

                        if (hasName && hasCategory) {
                            validEntries.push({
                                name: entry.name.trim(),
                                category: entry.category.map(cat => cat.value)
                            });
                        }

                    }
                    if (validEntries.length > 0) {
                        manualWordsPayload[type] = validEntries;
                    }
                }
                form.append('manualWords', JSON.stringify(manualWordsPayload));


                this.uploading = true;
                this.uploadProgress = 0;
                if (this.videoFiles.word_video) {
                    form.append('word_video', this.videoFiles.word_video);
                }
                if (this.videoFiles.example_video1) {
                    form.append('example_video1', this.videoFiles.example_video1);
                }
                if (this.videoFiles.example_video2) {
                    form.append('example_video2', this.videoFiles.example_video2);
                }
                if (this.videoFiles.example_video3) form.append('example_video3', this.videoFiles.example_video3);
                if (this.videoFiles.example_video4) form.append('example_video4', this.videoFiles.example_video4);
                // console.log("上傳影片資訊：");
                // console.log("example_video1:", this.videoFiles.example_video1);
                // console.log("example_video2:", this.videoFiles.example_video2);

                // 印出form變數
                form.forEach((value, key) => {
                    console.log(`${key}:`, value);
                });
                await instance.post('/create', form, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    },
                    timeout: 300000,
                    onUploadProgress: (progressEvent) => {
                        if (progressEvent.lengthComputable) {
                            this.uploadProgress = (progressEvent.loaded / progressEvent.total) * 100;
                        }
                    }
                });

                alert('新增成功');
                this.$router.push('/home');
            } catch (error) {
                console.error("新增失敗：", error);
                this.uploading = false;
                this.uploadProgress = 0;
                alert('新增失敗');
            }
        },
        addManualWord(type) {
            if (!this.manualWords[type]) this.$set(this.manualWords, type, []);
            this.manualWords[type].push({
                name: '',
                category: [],
                expanded: true
            });
        },
        removeManualWord(type, index) {
            this.manualWords[type].splice(index, 1);
        },
        canAddMore(type) {
            const entries = this.manualWords[type];
            if (!Array.isArray(entries) || entries.length === 0) return true;
            const last = entries[entries.length - 1];
            return last.name.trim() !== '' && Array.isArray(last.category) && last.category.length > 0;
        },
        toggleExpanded(type, index) {
            const entry = this.manualWords[type][index];
            if (entry) {
                entry.expanded = !entry.expanded;
            }
        }
    }
};
</script>

<style scoped>
.container {
    max-width: 720px;
}

.form-group label {
    font-weight: bold;
}
</style>