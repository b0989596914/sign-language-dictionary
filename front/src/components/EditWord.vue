<template>
  <div class="container mt-4">
    <h3 class="mb-3">編輯詞彙</h3>

    <div class="alert alert-info">
      <p>目前正在編輯詞彙編號：{{ formData.word_id }}</p>
    </div>

    <form @submit.prevent="submitEdit">
      <div class="form-group" v-for="field in fields" :key="field.key">
        <label :for="field.key">{{ field.label }}</label>

        <template v-if="field.key === 'category'">
          <multiselect v-model="formData.category" :options="categoryOptions" :multiple="true" placeholder="請選擇分類"
            label="label" track-by="value" class="form-control" />
        </template>

        <template v-else-if="field.key === 'word_type'">
          <!-- 詞性欄位 -->
          <multiselect v-if="field.key === 'word_type'" v-model="formData.word_type" :options="wordTypeOptions"
            placeholder="請選擇詞性" label="label" track-by="value" class="form-control" />

          <!-- 動詞類型下拉（僅當詞性為動詞時顯示） -->
          <div v-if="formData.word_type && formData.word_type.value === '動詞'" class="mt-2">
            <div class="mt-3">
              <label>動詞屬性：</label>
              <multiselect v-model="formData.verb_attributes" :options="verb_attributes" :multiple="true"
                placeholder="請選擇動詞屬性（可多選）" label="label" track-by="value" class="form-control" />
            </div>
            <label for="verb_type">動詞類型</label>
            <multiselect v-model="formData.verb_type" :options="verbSubtypes" placeholder="請選擇動詞類型" label="label"
              track-by="value" class="form-control" />
          </div>
          <!-- 明確在非動詞時不渲染 verb_attributes -->
          <div v-else>
            <input type="hidden" v-model="formData.verb_attributes" value="null" />
          </div>
        </template>

        <template v-else-if="field.key === 'feedback'">
          <textarea class="form-control" :id="field.key" v-model="formData[field.key]" rows="2" readonly></textarea>
        </template>

        <template v-else-if="multiSelectFields.includes(field.key)">
          <multiselect v-model="formData[field.key]" :options="wordOptions" :multiple="true" :close-on-select="false"
            :clear-on-select="false" :preserve-search="true" placeholder="請選擇" label="label" track-by="value"
            class="form-control" />

          <div v-for="(entry, index) in manualWords[field.key]" :key="index" class="border p-2 my-2 rounded bg-light">
            <div class="d-flex justify-content-between align-items-center">
              <strong>新增詞彙 #{{ index + 1 }}</strong>
              <div>
                <button type="button" class="btn btn-sm btn-outline-secondary me-2"
                  @click="toggleExpanded(field.key, index)">
                  {{ entry.expanded ? '收合' : '展開' }}
                </button>
                <button type="button" class="btn btn-sm btn-outline-danger" @click="removeManualWord(field.key, index)">
                  刪除
                </button>
              </div>
            </div>
            <div v-show="entry.expanded" class="mt-2">
              <input type="text" class="form-control mt-2" placeholder="請輸入新詞彙名稱" v-model="entry.name" />
              <multiselect v-model="entry.category" :options="categoryOptions" :multiple="true" label="label"
                track-by="value" placeholder="請選擇分類" class="form-control mt-2" />
            </div>
          </div>

          <button type="button" class="btn btn-sm btn-outline-primary mt-2" @click="addManualWord(field.key)"
            :disabled="!canAddMore(field.key)">
            新增詞彙
          </button>
        </template>

        <template v-else>
          <input v-if="!field.multiline" type="text" class="form-control" :id="field.key" v-model="formData[field.key]"
            :readonly="field.readonly" />
          <textarea v-else class="form-control" :id="field.key" v-model="formData[field.key]" rows="2"></textarea>
        </template>

        <!-- 新增影片區塊（用法欄位下方顯示） -->
        <div v-if="field.key === 'feedback'" class="mt-2">
          <label for="word_video">詞彙影片</label>
          <div v-if="formData.video_url">
            📁 已有原始影片，請先刪除後新增影片
            <button class="btn btn-outline-danger mx-2" type="button" @click="clearVideoFile(4)">刪除</button>
          </div>
          <div v-else>
            <input type="file" class="form-control" id="word_video" @change="handleFileChange($event, 4)"
              accept=".mp4, .mov" />
          </div>
        </div>

      </div>
      <div class="form-group" v-for="(ex, index) in exampleSentences" :key="index">
        <label>例句{{ index + 1 }} - 手語</label>
        <input type="text" class="form-control" v-model="ex.sign">

        <label>例句{{ index + 1 }} - 中文</label>
        <input type="text" class="form-control" v-model="ex.text">

        <label>例句{{ index + 1 }} - 備註</label>
        <textarea class="form-control" rows="2" v-model="ex.note"></textarea>

        <label>例句{{ index + 1 }} - 回饋 (readOnly)</label>
        <textarea class="form-control" rows="2" v-model="ex.reward" readonly></textarea>

        <label>例句{{ index + 1 }} - 影片連結</label>
        <div class=" w-100">
          <div v-if="ex.video_url" class="mb-2 text-muted">📁 已有原始影片，請先刪除後新增影片

            <button class="btn btn-outline-danger mx-2" type="button" @click="clearVideoFile(index)">
              刪除
            </button>
          </div>

          <div v-else>
            <input type="file" class="form-control w-100" @change="handleFileChange($event, index)"
              accept=".mp4, .mov" />
          </div>
        </div>
      </div>
      <div v-if="uploading" class="progress my-2">

        <label>上傳中，請稍候...</label>
        <div class="progress-bar" role="progressbar" :style="{ width: uploadProgress + '%' }">
          {{ uploadProgress.toFixed(0) }}%
        </div>
      </div>
      <button type="submit" class="btn btn-success mt-3">更新</button>
      <router-link to="/home" class="btn btn-link">返回列表</router-link>
    </form>
  </div>
</template>

<script>
// import axios from 'axios'
// import axios from '@/utils/axios';

import instance from '@/utils/axios.js';
import Multiselect from 'vue-multiselect';
import 'vue-multiselect/dist/vue-multiselect.min.css';
const baseVideoUrl = process.env.VUE_APP_VIDEO_URL;

// const baseUrl = process.env.VUE_APP_API_URL
export default {
  components: {
    Multiselect
  },
  data() {
    return {
      // 上傳進度條
      uploadProgress: 0,
      uploading: false,
      originalFormData: null, // ➜ 原始資料
      originalExampleSentences: [], // ➜ 原始例句資料
      formData: {
        word_id: '',
        word: '',
        category: '',
        word_type: '',
        verb_type: '',
        verb_attributes: [],
        handshape_left: '',
        handshape_right: '',
        handshape: '',
        useage: '',
        feedback: '',
        polysemy: [],
        synonym: [],
        antonym: [],
        related: [],
        variant: [],
        example_handshape1: '',
        example_text1: '',
        note1: '',
        reward1: '',
        example_handshape2: '',
        example_text2: '',
        note2: '',
        reward2: '',
      },
      fields: [
        { key: 'word_id', label: '詞彙編號(不可修改)', readonly: true },
        { key: 'word', label: '詞彙' },
        { key: 'category', label: '分類' },
        { key: 'word_type', label: '詞性' },
        { key: 'handshape', label: '手形' },
        { key: 'useage', label: '用法' },
        { key: 'feedback', label: '詞彙回饋 (readOnly)' },
        { key: 'polysemy', label: '多義詞', multiline: true },
        { key: 'synonym', label: '同義詞', multiline: true },
        { key: 'antonym', label: '反義詞', multiline: true },
        { key: 'related', label: '關聯詞', multiline: true },
        { key: 'variant', label: '變異詞', multiline: true },
      ],
      multiSelectFields: ['polysemy', 'synonym', 'antonym', 'related', 'variant'],
      categoryOptions: [],
      wordOptions: [],
      exampleSentences: [
        { sign: '', text: '', note: '', reward: '', video_url: '' },
        { sign: '', text: '', note: '', reward: '', video_url: '' },
        { sign: '', text: '', note: '', reward: '', video_url: '' },
        { sign: '', text: '', note: '', reward: '', video_url: '' }
      ],
      videoFiles: {
        word_video: null, // 詞彙影片
        example_video1: null,
        example_video2: null,
        example_video3: null,
        example_video4: null
      },
      deletedVideos: [], // 儲存要刪除的影片相對路徑
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
  async created() {
    const wordId = this.$route.params.id;
    await Promise.all([this.fetchCategories(), this.fetchWordOptions()]);

    await this.fetchFullWordData(wordId)
  },
  watch: {
    'formData.word_type'(newVal) {
      if (newVal?.value !== '動詞') {
        this.formData.verb_type = null; // 若不是動詞就清除 verb_type
        this.formData.verb_attributes = null;
      }
    }
  },
  methods: {
    toggleExpanded(type, index) {
      const entry = this.manualWords[type][index];
      if (entry) entry.expanded = !entry.expanded;
    },
    addManualWord(type) {
      if (!this.manualWords[type]) this.$set(this.manualWords, type, []);
      this.manualWords[type].push({ name: '', category: [], expanded: true });
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
    clearVideoFile(index) {
      if (index < 4) {    // 這裡是4個例句影片 0123
        const oldUrl = this.exampleSentences[index].video_url;

        // console.log("exampleSentences: " + this.exampleSentences[index]);

        // 底下的 oldUrl 是 /video/0_0_1-video1.mp4
        if (oldUrl && oldUrl.trim()) {
          this.deletedVideos.push(oldUrl); // 紀錄欲刪除影片
        }
        this.exampleSentences[index].video_url = ''; // 清除前端顯示
        // console.log("deletedVideos_clear: " + oldUrl);

      }
      else if (index === 4) {  // 這裡是詞彙影片
        const oldUrl = this.formData.video_url;
        if (oldUrl && oldUrl.trim()) {
          this.deletedVideos.push(oldUrl); // 紀錄欲刪除影片
        }
        this.formData.video_url = ''; // 清除前端顯示
      }
      else {
        console.error("無效的影片索引：", index);
        return;
      }
    },
    handleFileChange(event, index) {
      const file = event.target.files[0];
      if (!file) return;

      // 限制影片大小為200MB
      const maxSizeMB = 200;
      const maxSizeBytes = maxSizeMB * 1024 * 1024;

      if (file.size > maxSizeBytes) {
        alert(`上傳失敗：影片大小不能超過 ${maxSizeMB}MB。\n目前檔案大小為 ${(file.size / 1024 / 1024).toFixed(2)}MB`);
        event.target.value = ''; // 清空欄位
        return;
      }

      // 根據 index 分派到不同的暫存槽
      if (index >= 0 && index <= 3) {
        const key = `example_video${index + 1}`;
        this.videoFiles[key] = file;
      } else if (index === 4) {
        this.videoFiles.word_video = file;
      }
      else {
        console.error("無效的影片索引：", index);
        return;
      }
      // console.log("example_video1 檔案：", this.videoFiles.example_video1?.name);
    },

    async fetchFullWordData(wordId) {
      try {
        const res = await instance.get(`/Edit/full/${wordId}`);
        const { word, examples, relations } = res.data;
        console.log("fetchFullWordData:", res.data);

        // 主資料
        this.multiSelectFields.forEach(field => {
          if (word[field]) {
            const ids = word[field].split(',').filter(x => x);
            word[field] = this.wordOptions.filter(opt => ids.includes(opt.value));
          } else {
            word[field] = [];
          }
        });
        if (Array.isArray(word.category)) {
          word.category = word.category.map(id => {
            return this.categoryOptions.find(opt => opt.value === id);
          }).filter(Boolean);
        }
        this.formData = { ...this.formData, ...word };
        console.log("formData:", this.formData);

        // 詞性
        const typeFound = this.wordTypeOptions.find(opt => opt.value === word.word_type);
        this.formData.word_type = typeFound || null;

        // 動詞類型與屬性
        if (word.word_type === '動詞' && word.verb_type) {
          const found = this.verbSubtypes.find(opt => opt.value === word.verb_type);
          this.formData.verb_type = found || null;
        }
        if (word.word_type === '動詞' && word.verb_attributes) {
          const attrs = word.verb_attributes.split(',').map(attr => attr.trim());
          this.formData.verb_attributes = this.verb_attributes.filter(opt => attrs.includes(opt.value));
        } else {
          this.formData.verb_attributes = null; // 改為 null
        }

        console.log("word.verb_attributes:", word.verb_attributes);
        console.log("formData.verb_attributes:", this.formData.verb_attributes);

        // 例句
        const defaultExample = { sign: '', text: '', note: '', reward: '', video_url: '' };
        this.exampleSentences = examples.slice(0, 4);
        while (this.exampleSentences.length < 4) {
          this.exampleSentences.push({ ...defaultExample });
        }
        this.originalExampleSentences = JSON.parse(JSON.stringify(this.exampleSentences));

        // 關聯詞
        const grouped = {
          polysemy: [], synonym: [], antonym: [], related: [], variant: []
        };
        relations.forEach(rel => {
          if (grouped[rel.relation_type]) {
            grouped[rel.relation_type].push({
              value: rel.word_id,
              label: `${rel.word_id} - ${rel.word}`
            });
          }
        });
        this.originalrelations = JSON.parse(JSON.stringify(grouped));
        for (const key of Object.keys(grouped)) {
          this.formData[key] = grouped[key];
        }
        this.originalFormData = JSON.parse(JSON.stringify(this.formData));
      } catch (err) {
        alert("資料載入失敗");
      }
    },

    async fetchCategories() {
      try {
        const res = await instance.get('/Create/categories');
        this.categoryOptions = res.data.map(cat => ({
          value: cat.id,
          label: cat.categoryName
        }));
      } catch (error) {
        console.error("分類載入失敗：", error);
      }
    },

    async fetchWordOptions() {
      try {
        const res = await instance.get('/Create/option');
        const list = Array.isArray(res.data) ? res.data : res.data.data || [];
        this.wordOptions = list.map((item) => ({
          value: item.word_id,
          label: `${item.word_id} - ${item.word}`
        }));
      } catch (error) {
        console.error("詞彙選項載入失敗：", error);
      }
    },

    buildRelationPayload(formData) {
      const types = ['polysemy', 'synonym', 'antonym', 'related', 'variant'];
      return types.map(type => ({
        type,
        word_id2_list: formData[type].map(item => item.value)
      }));
    },

    async submitEdit() {
      const idRule = /^[a-zA-Z0-9_-]+$/;

      if (!idRule.test(this.formData.word_id)) {
        alert("詞彙編號格式錯誤：不可包含空格或特殊符號（僅限英數字、底線、橫線）");
        return;
      }
      try {
        // --- 防呆：單句完整性檢查 (確保手語/中文成對出現) ---
        for (let i = 0; i < this.exampleSentences.length; i++) {
          const ex = this.exampleSentences[i];
          const hasSign = ex.sign && ex.sign.trim();
          const hasText = ex.text && ex.text.trim();

          // 狀況 A：填了手語，沒填中文
          if (hasSign && !hasText) {
            alert(`資料不完整：\n您在「例句 ${i + 1}」填寫了手語，但未填寫中文。\n請補上「例句 ${i + 1} - 中文」。`);
            this.uploading = false;
            return;
          }
          // 狀況 B：填了中文，沒填手語 
          if (!hasSign && hasText) {
            alert(`資料不完整：\n您在「例句 ${i + 1}」填寫了中文，但未填寫手語。\n請補上「例句 ${i + 1} - 手語」。`);
            this.uploading = false;
            return;
          }
        }

        // --- 防呆檢查 (嚴格順序：1->2->3->4) ---
        const isExEmpty = (ex) => {
          const signEmpty = !ex.sign || !ex.sign.trim();
          const textEmpty = !ex.text || !ex.text.trim();
          return signEmpty && textEmpty;
        };
        const firstEmptyIndex = this.exampleSentences.findIndex(ex => isExEmpty(ex));
        if (firstEmptyIndex !== -1) {
          for (let i = firstEmptyIndex + 1; i < this.exampleSentences.length; i++) {
            if (!isExEmpty(this.exampleSentences[i])) {
              alert(`填寫順序錯誤：\n您在「例句 ${firstEmptyIndex + 1}」尚未填寫的情況下，填寫了「例句 ${i + 1}」。\n請依序填寫。`);
              this.uploading = false;
              return;
            }
          }
        }

        // 動詞檢查
        if (this.formData.word_type?.value === '動詞') {
          if (!this.formData.verb_type) {
            alert("請選擇動詞類型");
            return;
          }
          if (!this.formData.verb_attributes || this.formData.verb_attributes.length === 0) {
            alert("請至少選擇一個動詞屬性");
            return;
          }
        } else {
          this.formData.verb_attributes = null; // 強制設為 null
        }

        const form = new FormData();
        this.uploading = true;
        this.uploadProgress = 0;

        const wordData = { ...this.formData };

        // 處理單選欄位
        ['word_type', 'verb_type'].forEach(field => {
          if (wordData[field]) {
            wordData[field] = wordData[field].value || null;
          } else {
            wordData[field] = null;
          }
        });

        // 處理 multiSelectFields
        this.multiSelectFields.forEach(type => {
          if (Array.isArray(wordData[type])) {
            wordData[type] = wordData[type].map(item => item.value);
          }
        });

        // 處理 verb_attributes
        if (wordData.verb_attributes && Array.isArray(wordData.verb_attributes)) {
          wordData.verb_attributes = wordData.verb_attributes
            .map(item => item.value || String(item))
            .join(', ') || null;
        } else {
          wordData.verb_attributes = null;
        }

        // 處理 category
        if (Array.isArray(wordData.category)) {
          wordData.category = JSON.stringify(wordData.category);
        }

        form.append('word', JSON.stringify(wordData));
        form.append('examples', JSON.stringify(this.exampleSentences));
        form.append('relations', JSON.stringify(this.buildRelationPayload(this.formData)));
        if (this.videoFiles.word_video) form.append('word_video', this.videoFiles.word_video);
        if (this.videoFiles.example_video1) form.append('example_video1', this.videoFiles.example_video1);
        if (this.videoFiles.example_video2) form.append('example_video2', this.videoFiles.example_video2);
        if (this.videoFiles.example_video3) form.append('example_video3', this.videoFiles.example_video3);
        if (this.videoFiles.example_video4) form.append('example_video4', this.videoFiles.example_video4);
        if (this.deletedVideos.length > 0) form.append('deleted_videos', JSON.stringify(this.deletedVideos));

        const manualWordsPayload = {};
        for (const type in this.manualWords) {
          manualWordsPayload[type] = this.manualWords[type].map(entry => ({
            name: entry.name,
            category: entry.category.map(cat => cat.value)
          }));
        }
        form.append('manualWords', JSON.stringify(manualWordsPayload));

        // 檢查是否有變更
        const isRelationEqual = (a, b) => {
          const getIds = (arr) => (arr || []).map(item => item.value).sort().join(',');
          return getIds(a) === getIds(b);
        };
        const hasChanges = () => {
          // 1. 檢查基本欄位與多選欄位
          for (const key of Object.keys(this.formData)) {
            if (this.multiSelectFields.includes(key)) {
              if (!isRelationEqual(this.formData[key], this.originalFormData[key])) return true;
            } else if (JSON.stringify(this.formData[key]) !== JSON.stringify(this.originalFormData[key])) {
              return true;
            }
          }

          // 2. 檢查例句文字內容
          if (JSON.stringify(this.exampleSentences) !== JSON.stringify(this.originalExampleSentences)) return true;

          // 3. 檢查是否有刪除影片的紀錄
          if (this.deletedVideos.length > 0) return true;

          // 4. 檢查是否有任何新上傳的影片檔案
          const hasNewVideo = Object.values(this.videoFiles).some(file => file !== null);
          if (hasNewVideo) return true;

          // 5. 檢查是否有新增詞彙
          for (const type in this.manualWords) {
            if (this.manualWords[type].length > 0) return true;
          }

          return false;
        };

        if (!hasChanges()) {
          alert("不可修改：您沒有變更任何內容");
          this.uploading = false;
          return;
        }

        const response = await instance.post('/Edit/fullUpdate', form, {
          headers: { 'Content-Type': 'multipart/form-data' },
          timeout: 300000,
          onUploadProgress: (progressEvent) => {
            if (progressEvent.lengthComputable) {
              this.uploadProgress = (progressEvent.loaded / progressEvent.total) * 100;
            }
          },
          transformRequest: [(data, headers) => {
            delete headers['Content-Type'];
            return data;
          }]
        });

        console.log("後端回應：", response.data);
        alert('更新成功');
        this.$router.push('/home');
      } catch (err) {
        this.uploading = false;
        this.uploadProgress = 0;
        console.error('提交錯誤：', err.response?.data || err.message);
        alert(`更新失敗：${err.response?.data?.message || '請檢查輸入資料'}`);
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
