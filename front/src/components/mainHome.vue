<template>
  <div class="ta_container mt-3 ">
    <div v-if="role === 'censor_team_leader'" class="my-2">
      <label for="statusFilter">審查狀態：</label>
      <select v-model="selectedCheckWord" id="statusFilter" class="form-select form-select-sm"
        style="width: 180px; display: inline-block;">
        <option value="">全部狀態</option>
        <option value="1">組長需審查</option>
        <option value="2">需修改</option>
        <option value="3">總審查中</option>
        <option value="4">通過</option>
      </select>
    </div>
    <div v-else-if="role === 'chief_censor'" class="my-2">
      <label for="statusFilter">審查狀態：</label>
      <select v-model="selectedCheckWord" id="statusFilter" class="form-select form-select-sm"
        style="width: 180px; display: inline-block;">
        <option value="">全部狀態</option>
        <option value="3">需總審查</option>
        <option value="2">需修改</option>
        <option value="1">組長審查中</option>
        <option value="4">通過</option>
      </select>
    </div>
    <div v-else-if="role === 'committee'" class="my-2">
      <label for="statusFilter">審查狀態：</label>
      <select v-model="selectedCheckWord" id="statusFilter" class="form-select form-select-sm"
        style="width: 180px; display: inline-block;">
        <option value="">全部狀態</option>
        <option value="2">需修改</option>
        <option value="1">組長審查中</option>
        <option value="3">需總審查</option>
        <option value="4">通過</option>
      </select>
    </div>
    <div v-else-if="role === 'admin'" class="my-2">
      <label for="statusFilter">審查狀態：</label>
      <select v-model="selectedCheckWord" id="statusFilter" class="form-select form-select-sm"
        style="width: 180px; display: inline-block;">
        <option value="">全部狀態</option>
        <option value="5">需更改權限</option>
        <option value="2">需修改</option>
        <option value="1">組長審查中</option>
        <option value="3">需總審查</option>
        <option value="4">通過</option>
      </select>
    </div>
    <div v-else-if="role === 'special_professor'" class="my-2">
      <label for="statusFilter">審查狀態：</label>
      <select v-model="selectedCheckWord" id="statusFilter" class="form-select form-select-sm"
        style="width: 180px; display: inline-block;">
        <option value="">全部狀態</option>
        <option value="1">組長審查中</option>
        <option value="2">需修改</option>
        <option value="3">需總審查</option>
        <option value="4">通過</option>
      </select>
    </div>
    <div v-if="searchKeyword && filteredTableData.length === 0" class="alert alert-warning text-center mt-3">
      找不到符合的資料
    </div>

    <div v-else class="table-container" ref="tableContainer">
      <table class="table table-bordered text-center align-middle ">
        <thead>
          <tr>
            <th rowspan="2" id="th_id" style="white-space: normal; width:45px;">審核狀態</th>
            <th rowspan="2" id="th_id">ID</th>
            <th rowspan="2" id="th_word_id">編號</th>
            <th rowspan="2" id="th_word">詞彙</th>
            <th rowspan="2" id="th_word_category">分類</th>
            <th rowspan="2" id="th_w
            ord_word_type">詞性</th>
            <th rowspan="2" id="th_word_handshape">手形</th>
            <th rowspan="2" id="th_word_useage">用法</th>
            <th rowspan="2" id="th_word_feedback">回饋</th>
            <th rowspan="2" id="th_word_video">影片</th>
            <th colspan="2">多義詞</th>
            <th colspan="2">同義詞</th>
            <th colspan="2">反義詞</th>
            <th colspan="2">關聯詞</th>
            <th colspan="2">變異詞</th>
            <th colspan="6">例句</th>
            <!-- <th colspan="1">例句</th> -->
            <!-- 
            <th rowspan="2" id="th_ex_note">備註</th>
            <th rowspan="2" id="th_ex_reward">回饋</th>
            <th rowspan="2" id="th_ex_video_url">影片連結</th> -->
            <th rowspan="2" id="th_word_operation">操作</th>
          </tr>
          <tr>
            <th id="th_polysemy_word_id">編號</th>
            <th id="th_polysemy_word">詞彙</th>
            <th id="th_synonym_word_id">編號</th>
            <th id="th_synonym_word">詞彙</th>
            <th id="th_antonym_word_id">編號</th>
            <th id="th_antonym_word">詞彙</th>
            <th id="th_related_word_id">編號</th>
            <th id="th_related_word">詞彙</th>
            <th id="th_variant_word_id">編號</th>
            <th id="th_variant_word">詞彙</th>
            <th id="th_ex_id">編號</th>
            <!-- <th id="th_ex_id">編號 | 手語 | 中文</th> -->

            <th id="th_ex_sign">手語</th>
            <th id="th_ex_text">中文</th>

            <th rowspan="2" id="th_ex_note">備註</th>
            <th rowspan="2" id="th_ex_reward">回饋</th>
            <th rowspan="2" id="th_ex_video_url">影片</th>
          </tr>

        </thead>

        <tbody v-for="(item, index) in filteredTableData" :key="item.word_id" class="inner-example-table"
          :data-word-id="item.word_id" :id="`word-${item.word_id}`">

          <!-- <template v-for="(item, index) in tableData" :key="item.word_id"> -->
          <tr v-for="i in mergeRowsByGroup(item)" :key="item.word_id + '-row-' + i" :id="`th-${item.word_id}`">

            <!-- 只在第一行顯示主資料，並設定row span -->
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)">
              <div
                style="text-align: center; display: inline-block;display: flex; justify-content: center; align-items: center; height: 100%;">
                <!-- 管理員、委員：只能看到狀態數值 -->
                <template
                  v-if="(role === 'admin' || role === 'committee') && (item.check_word === '1' || item.check_word === '3')">
                  {{ resolveCheckWordStatus(item.check_word) }}
                </template>

                <!-- 總審查：check_word 為 3 時可操作（如同組長的權限） -->
                <!-- 總審查權限 -->
                <template v-else-if="role === 'chief_censor' && item.check_word === '3'">
                  <select v-model="item.check_word" @change="updateCheckWord(item)"
                    style="width: 100%; font-size: 12px;">
                    <option disabled value="">請選擇</option>
                    <option value="4">通過</option>
                    <option value="2">需修改</option>
                  </select>
                </template>

                <!-- 審查組長：只有當 check_word 為 1 且為自己組的詞彙才可操作 -->
                <!-- 審查組長權限 -->
                <template
                  v-else-if="role === 'censor_team_leader' && item.check_word === '1' && String(item.added_person_group_id) === String(userGroupId)">
                  <select v-model="item.check_word" @change="updateCheckWord(item)"
                    style="width: 100%; font-size: 12px;">
                    <option disabled value="">請選擇</option>
                    <option value="3">通過</option>
                    <option value="2">需修改</option>
                  </select>
                </template>

                <!-- special_professor 權限 -->
                <template
                  v-else-if="role === 'special_professor' && (item.check_word === '1' || item.check_word === '3')">
                  <select v-model="item.check_word" @change="updateCheckWord(item)"
                    style="width: 100%; font-size: 12px;">
                    <option disabled value="">請選擇</option>
                    <option value="4">通過</option>
                  </select>
                </template>

                <!-- 不同組長審查中 -->
                <template
                  v-else-if="role === 'censor_team_leader' && item.check_word === '1' && String(item.added_person_group_id) !== String(userGroupId)">
                  <span>其他組長審查</span>
                </template>

                <!-- 總審查中 -->
                <template v-else-if="item.check_word === '3'">
                  <span>總審查</span>
                </template>

                <!-- 需修改 -->
                <template v-else-if="item.check_word === '2'">
                  <span style="color: red;">需修改</span>
                </template>

                <!-- 通過 -->
                <template v-else-if="item.check_word === '4'">
                  <span style="color: green;">通過</span>
                </template>

                <!-- 預設 -->
                <template v-else>
                  <span>組長審查</span>
                </template>
              </div>
            </td>

            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)" style=" text-align: center;">{{ index + 1 }}</td>
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)" style=" text-align: center;">{{ item.word_id }}</td>
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)">{{ item.word }}</td>
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)">{{ item.category_names }}</td>
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)">{{ item.word_type }}<span
                v-if="item.word_type === '動詞' && item.verb_attributes && item.verb_type">/{{ item.verb_attributes }}/{{
                  item.verb_type
                }}</span></td>
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)">{{ item.handshape }}</td>
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)">{{ item.useage }}</td>

            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)" class="clickable-cell">
              <span v-if="item.feedback" @click="showPopup(item.feedback, '詞彙回饋')" class="popup-link green">
                查看
              </span>
              <span v-else>{{ item.feedback || '' }}</span>
            </td>

            <!-- 詞彙影片顯示 -->
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)">
              <router-link v-if="item.video_url" :to="`/Video/${encodeURIComponent(item.video_url)}`"
                @click.native="storeScrollTarget(item.word_id)">
                <span class="popup-link green">影片</span>
              </router-link>
            </td>


            <!-- 各種關聯詞(有多行) -->
            <template v-for="type in multiSelectFields" :key="`id-${type}-${i}`">
              <td>
                <button v-if="getAssociationsByType(item, type)[i - 1]?.related_word_id"
                  @click="scrollToWord(getAssociationsByType(item, type)[i - 1].related_word_id)"
                  style="all: unset; border: none; background: none; color: black; cursor: pointer;">
                  {{ getAssociationsByType(item, type)[i - 1].related_word_id }}
                </button>
              </td>
              <td>
                <button v-if="getAssociationsByType(item, type)[i - 1]?.related_word"
                  @click="scrollToWord(getAssociationsByType(item, type)[i - 1].related_word_id)"
                  style="all: unset; border: none; background: none; color: black; cursor: pointer;">
                  {{ getAssociationsByType(item, type)[i - 1].related_word }}
                </button>
              </td>
            </template>

            <!-- 例句-只顯示在第一欄，但可以分格子 -->
            <!-- <template v-if="i === 1" >
              <template v-for="(ex, i) in item.examples" :key="i">
                <tr :rowspan="mergeRowsByGroup(item)">
                  <td style="width:8%">{{ i + 1 }}</td>
                  <td>{{ ex.sign }}</td>
                  <td>{{ ex.text }}</td>
                </tr>
              </template>
            </template> -->


            <!-- <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)" slice="getRowspan_example(item)">{{ item.examples?.[i-1]?.example_index || '' }}</td>
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)">{{ item.examples?.[i-1]?.sign || '' }}</td>
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)">{{ item.examples?.[i-1]?.text || '' }}</td> -->
            <!-- <td v-if="i === 2" :rowspan="mergeRowsByGroup(item)">{{ item.examples?.[i-1]?.example_index || '' }}</td>
            <td v-if="i === 2" :rowspan="mergeRowsByGroup(item)">{{ item.examples?.[i-1]?.sign || '' }}</td>
            <td v-if="i === 2" :rowspan="mergeRowsByGroup(item)">{{ item.examples?.[i-1]?.text || '' }}</td> -->

            <!-- <tr v-for="(ex, i) in item.examples" :key="i" >
              <td>{{ i + 1 }}</td>
              <td>{{ ex.sign }}</td>
              <td>{{ ex.text }}</td>
            </tr> -->


            <!-- 例句(編號、手語、中文) -->
            <td>{{ i <= item.examples.length ? i : '' }}</td>
            <td>{{ item.examples[i - 1]?.sign || '' }}</td>
            <td>{{ item.examples[i - 1]?.text || '' }}</td>


            <!-- 例句(備註) -->
            <td class="clickable-cell">
              <span v-if="item.examples[i - 1]?.note" @click="showPopup(item.examples[i - 1].note, '備註')"
                class="popup-link blue">
                查看
              </span>
              <span v-else>{{ item.examples[i - 1]?.note || '' }}</span>
            </td>

            <!-- 例句(回饋) -->
            <td class="clickable-cell">
              <span v-if="item.examples[i - 1]?.reward" @click="showPopup(item.examples[i - 1].reward, '回饋')"
                class="popup-link green">
                查看
              </span>
              <span v-else>{{ item.examples[i - 1]?.reward || '' }}</span>
            </td>

            <!-- 例句(影片) -->
            <td v-if="item.examples[i - 1]?.video_url">
              <router-link :to="`/Video/${encodeURIComponent(item.examples[i - 1].video_url)}`"
                @click.native="storeScrollTarget(item.word_id)">
                <!-- <img src="../assets/video.png" alt="觀看影片"
                  style="width: 22px; cursor: pointer; display: block; margin: auto;" /> -->
                <span class="popup-link green"> 影片 </span>
              </router-link>
            </td>
            <td v-else></td>

            <!-- 操作 -->
            <td v-if="i === 1" :rowspan="mergeRowsByGroup(item)">
              <div class="d-flex justify-content-center gap-1 flex-wrap">
                <!-- 若為 committee，且只能操作自己建立的 -->
                <template v-if="role === 'committee'">
                  <template v-if="String(item.added_person) === String(user_id)">
                    <button class="btn btn-primary btn-sm px-2 py-0" @click="goToEdit(item.word_id)">編輯</button>
                    <button class="btn btn-danger btn-sm px-2 py-0" @click="deleteItem(item)">刪除</button>
                  </template>
                  <template v-else>
                    <span class="text-muted">無權限</span>
                  </template>
                </template>

                <template v-else-if="role === 'censor_team_leader'">
                  <template v-if="String(item.added_person_group_id) === String(userGroupId)">
                    <button class="btn btn-primary btn-sm px-2 py-0" @click="goToEdit(item.word_id)">編輯</button>
                    <button class="btn btn-danger btn-sm px-2 py-0" @click="deleteItem(item)">刪除</button>
                  </template>
                  <template v-else>
                    <span class="text-muted">無權限</span>
                  </template>
                </template>
                <!-- 其他角色正常顯示（非 committee） -->
                <template v-else>
                  <button class="btn btn-primary btn-sm px-2 py-0" @click="goToEdit(item.word_id)">編輯</button>
                  <button class="btn btn-danger btn-sm px-2 py-0" @click="deleteItem(item)">刪除</button>
                </template>
                <template v-if="role === 'admin'">
                  <button class="btn btn-outline-secondary btn-sm px-2 py-0"
                    @click="goTochange(item.word_id)">切換權限</button>
                </template>
              </div>
            </td>
            <!-- </template> -->
          </tr>
          <!-- <template v-if="check(item)">
            <tr v-for="i in (getRowspan_example(item) - 1)" :key="item.word_id + '-row-' + i">
              <td :rowspan="getRowspan_example(item)">{{ item.examples?.[i]?.example_index || '' }}</td>
              <td :rowspan="getRowspan_example(item)">{{ item.examples?.[i]?.sign || '' }}</td>
              <td :rowspan="getRowspan_example(item)">{{ item.examples?.[i]?.text || '' }}</td>
            </tr>
          </template> -->
        </tbody>
      </table>
    </div>
  </div>

  <!-- Popup Modal -->
  <div v-if="showPopupWindow" class="popup-overlay" @click.self="closePopup">
    <div class="popup-content">
      <h5>{{ popupTitle }}</h5>
      <p>{{ popupContent }}</p>
      <button @click="closePopup" class="btn btn-secondary btn-sm">關閉</button>
    </div>
  </div>
</template>

<script>
import instance from '@/utils/axios.js';
import { fetchSessionRole } from '@/utils/auth.js';

export default {
  props: {
    searchKeyword: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      tableData: [],
      role: '',
      user_id: '',
      userGroupId: '', // for censor_team_leader 判斷權限用
      filterMode: false,
      selectedCheckWord: '', // '1'、'2'、'3'、'4' 或 '' 代表不篩選，新增'5'專給 'admin' 篩選需要更換權限的使用
      popupContent: '',
      popupTitle: '',
      showPopupWindow: false,

      multiSelectFields: ['polysemy', 'synonym', 'antonym', 'related', 'variant'],
    }
  },
  async mounted() {

    const sessionInfo = await fetchSessionRole(); // 取回身份
    this.role = sessionInfo.role;
    this.user_id = sessionInfo.user_id;
    this.userGroupId = sessionInfo.group_id;

    await this.fetchData();

    if (!this.searchKeyword && this.$route.query.keyword) {
      this.$emit("update:searchKeyword", this.$route.query.keyword);
    }

    const targetId = this.$route.query.scrollTo;
    if (targetId) {
      this.$nextTick(() => {
        const el = document.getElementById(`word-${targetId}`);
        if (el) {
          el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
      });
    }

    const scrollToId = localStorage.getItem('scrollToId');
    if (scrollToId) {
      this.$nextTick(() => {
        const el = document.getElementById(`th-${scrollToId}`);
        if (el) {
          el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
        localStorage.removeItem('scrollToId');
      });
    }

  },
  computed: {
    displayWordType() {
      if (this.formData.word_type === '動詞' && this.formData.verb_attributes && this.formData.verb_type) {
        return `${this.formData.word_type} / ${this.formData.verb_attributes} / ${this.formData.verb_type}`;
      }
      return this.formData.word_type;
    },
    filteredTableData() {
      let data = this.tableData;

      // admin 特殊篩選
      if (this.role === 'admin' && this.selectedCheckWord === '5') {
        console.log("test for admin and selectedCheckWord")
        // 過濾出 check_word 為 5 且 added_person 的角色為 admin 的詞彙
        data = data.filter(item =>
          item.added_person_role === 'admin' || ([1, 3].includes(Number(item.added_person))) // 1: 管理員, 3: 總審查
        );

      }

      else if (this.role === 'censor_team_leader' && this.selectedCheckWord !== '') {
        data = data.filter(item =>
          String(item.check_word) === this.selectedCheckWord &&
          String(item.added_person_group_id) === String(this.userGroupId)
        );
      }
      else if (this.role === 'committee' && this.selectedCheckWord !== '') {
        data = data.filter(item =>
          String(item.check_word) === this.selectedCheckWord &&
          String(item.added_person) === String(this.user_id)
        );
      }
      // pro、總審、管理員的過濾
      else if ((this.role === 'chief_censor' || this.role === 'admin' || this.role === 'special_professor') && this.selectedCheckWord !== '') {
        data = data.filter(item => String(item.check_word) === this.selectedCheckWord);
      }

      const keyword = (this.searchKeyword || '').toString().trim().toLowerCase();

      if (!keyword) return data;

      // 只搜尋詞彙本身（詞彙、編號），不搜尋用法、例句、關聯詞等內容
      return data.filter(item =>
        item.word?.toLowerCase().includes(keyword) ||
        item.word_id?.toLowerCase().includes(keyword)
      );
    }
  },
  methods: {
    showPopup(content, title) {
      this.popupContent = content;
      this.popupTitle = title;
      this.showPopupWindow = true;
    },
    closePopup() {
      this.showPopupWindow = false;
    },
    resolveCheckWordStatus(status) {
      const statusMap = {
        1: '組長審查',
        2: '需修改',
        3: '總審查',
        4: '通過',
      };
      return statusMap[status] || '未知狀態';
    },
    async updateCheckWord(item) {
      const newStatus = item.check_word;
      try {
        const res = await instance.post('/words/update-check', {
          word_id: item.word_id,
          added_person_groupId: item.added_person_group_id,
          check_word: newStatus
        });
        alert('狀態更新成功');

        // 如果是 special_professor，選擇後直接設為通過，並清除回饋
        if (this.role === 'special_professor' && newStatus === '4') {
          await instance.post('/word/deleteFeedback', {
            word_id: item.word_id,
            added_person_groupId: item.added_person_group_id
          });
          console.log('回饋已刪除');
          // 更新本地資料
          item.feedback = null;
          if (item.examples && item.examples.length > 0) {
            item.examples.forEach(ex => {
              ex.reward = null;
            });
          }
        }
        // 如果是組長且選擇「需修改」，跳轉去填寫回饋
        if (
          this.role === 'censor_team_leader' &&
          newStatus === '2' &&
          String(item.added_person_group_id) === String(this.userGroupId)
        ) {
          this.$router.push(`/feedback/${item.word_id}`);
        }
        // 如果是總審查且選擇「需修改」，跳轉去填寫回饋
        else if (
          this.role === 'chief_censor' &&
          newStatus === '2'
        ) {
          this.$router.push(`/feedback/${item.word_id}`);
        }

        // await this.fetchData();
        // 根據身份與選擇的 value 處理邏輯
        // 組長通過到總審查或總審查通過時，清除回饋
        if (
          (this.role === 'censor_team_leader' && newStatus === '3') ||
          (this.role === 'chief_censor' && newStatus === '4')
        ) {
          // 呼叫刪除回饋
          await instance.post('/word/deleteFeedback', {
            word_id: item.word_id,
            added_person_groupId: item.added_person_group_id
          });
          console.log('回饋已刪除');
          // 更新本地資料（避免刷新）
          item.feedback = null;
          if (item.examples && item.examples.length > 0) {
            item.examples.forEach(ex => {
              ex.reward = null;
            });
          }
        }


      } catch (error) {
        console.error('狀態更新失敗', error);
        alert('狀態更新失敗');
      }
    },
    async scrollToWord(wordId) {
      let targetRow = document.querySelector(`[data-word-id="${wordId}"]`);

      // 目標詞彙被搜尋或審核狀態篩選隱藏時，改為搜尋該詞彙再捲動過去
      if (!targetRow && this.tableData.some(item => item.word_id === wordId)) {
        this.selectedCheckWord = '';
        await this.$router.push({ path: '/home', query: { keyword: wordId } });
        await this.$nextTick();
        await this.$nextTick();
        targetRow = document.querySelector(`[data-word-id="${wordId}"]`);
      }

      if (targetRow) {
        const container = this.$refs.tableContainer;
        const containerTop = container.getBoundingClientRect().top;
        const targetTop = targetRow.getBoundingClientRect().top;
        const scrollOffset = targetTop - containerTop + container.scrollTop - 50; // 50 可調整為 sticky header 高度

        container.scrollTo({
          top: scrollOffset,
          behavior: 'smooth'
        });
      } else {
        alert(`找不到詞彙 ${wordId}`);
      }
    },
    storeScrollTarget(word_id) {
      if (word_id) {
        localStorage.setItem("scrollToId", word_id);
      }
      this.$router.push(`/video/${item.word_id}`);
    },

    // async confirmWord(item) {
    //   try {
    //     const response = await instance.post('/confirm-word?id=' + item.word_id);
    //     // console.log('確認成功', response.data);
    //     item.check_word = true;  // 變更按鈕狀態
    //   } catch (error) {
    //     console.error('確認失敗', error);
    //   }
    // },
    // getSplitExamples(item) {
    //   const total = this.getRowspan(item);
    //   const examples = item.examples || [];

    //   // 如果例句不足 total，補上 undefined 讓每行都能被均分
    //   const filled = [...examples];
    //   while (filled.length < total) {
    //     filled.push(null);
    //   }
    //   return filled;
    // },
    check(item) {
      return this.getRowspan_example(item) > 1;
    },
    mergeRowsByGroup(item) {
      const maxLen = Math.max(
        this.getRowspan(item),
        item.examples.length || 0,
        1
      );
      return maxLen;
    },
    // getAssociationExamples(item, index) {
    //   const types = ['polysemy', 'synonym', 'antonym', 'related', 'variant'];
    //   for (const type of types) {
    //     const assoc = this.getAssociationsByType(item, type)[index];
    //     if (assoc?.examples?.length) {
    //       return assoc.examples;
    //     }
    //   }
    //   return [];
    // },
    getRowspan(item) {
      // 找出五種關聯詞裡最多的數量
      return Math.max(
        this.getAssociationsByType(item, 'polysemy').length,
        this.getAssociationsByType(item, 'synonym').length,
        this.getAssociationsByType(item, 'antonym').length,
        this.getAssociationsByType(item, 'related').length,
        this.getAssociationsByType(item, 'variant').length,
        1  // 至少一行
      ); // 加上例句行數;
    },
    getAssociationsByType(item, type) {
      return (item.associations || []).filter(a => a.relation_type === type);
    },

    getRowspan_example(item) {
      // 如果例句有資料，就回傳例句的數量；否則至少1行
      return (item.examples && item.examples.length > 0) ? item.examples.length : 1;
    },

    async fetchData() {

      try {
        const response = await instance.get("/fetchData");
        // console.log("獲取資料成功：", response.data);
        console.log("selectedCheckWord資料：", this.selectedCheckWord);
        const data = await response.data;
        // console.log("獲取資料成功：", data);

        // 預處理關聯詞，減少渲染時的計算
        this.tableData = data.map(item => {
          const associations = item.associations || [];
          const groupedAssociations = {
            polysemy: [],
            synonym: [],
            antonym: [],
            related: [],
            variant: []
          };

          // 一次性分類所有關聯詞
          associations.forEach(assoc => {
            if (groupedAssociations[assoc.relation_type]) {
              groupedAssociations[assoc.relation_type].push(assoc);
            }
          });

          return {
            ...item,
            groupedAssociations, // 直接使用預處理好的分類
            associations // 保留原始數據
          };
        });

      } catch (error) {
        console.error("獲取資料失敗：", error);
      }
    },
    goToEdit(wordId) {
      this.$router.push(`/Edit/${wordId}`);
    },
    goTochange(wordId) {
      this.$router.push(`/Change/${wordId}`);
    },

    // 刪除資料
    async deleteItem(item) {

      try {
        let message = `確定要刪除 ${item.word_id} ${item.word}`;

        if (!confirm(message)) return;

        const response = await instance.delete(`/words/${item.word_id}`);

        if (response.status === 200) {
          console.log("成功刪除");

          this.fetchData();
        } else {
          console.error("刪除失敗：", response.status);
          alert('刪除失敗');
        }

      } catch (error) {
        console.error("刪除失敗：", error);
        alert('刪除失敗');
      }
    },

  }
}
</script>

<style>
:root {
  --main-textcolor: #15121f;
  --second-bgcolor: #e9f1d6;
  --white: #ffffff;
}

select option[disabled] {
  color: gray;
}

.container-fluid {
  padding-top: 80px;
}

.ta_container {
  width: 98%;
  /* 寬度97%，左右各留1.5% */
  margin: 0 auto;
  padding-bottom: 10px;
  /* 自動置中 */
  /* overflow-x: auto; */
}


table {
  width: 100%;
  /* border-collapse: collapse; */
  /* width: auto; */
  /* table-layout: auto; */
  table-layout: fixed;
  /* 關鍵：避免欄寬隨內容改變造成跳動 */

}

/* 有需要的話，可以讓內容超過一定寬度時自動換行 */
.table-container {
  overflow-y: auto;
  margin-top: 2vh;
  /* 離頂部 10% 高度 */
  height: calc(98vh - 10vh);
  /* 剩下 90% 畫面高度 */
  position: relative;
}

/* 表格內容 */
td {
  /* padding: 4px 6px; */
  line-height: 1.4;

  font-size: 12px;
  word-break: break-word;
  vertical-align: middle;
  text-align: center;
}

/* 標頭 */
thead tr:nth-child(1) th {
  top: 0;
  z-index: 10;
  font-size: 13px;
  background-color: var(--second-bgcolor);
  white-space: nowrap;
}

thead tr:nth-child(2) th {
  top: 35px;
  /* 根據實際第一行 th 高度調整 */
  z-index: 9;
  font-size: 13px;
  background-color: var(--second-bgcolor);
  white-space: nowrap;
}

thead th {
  position: sticky;
  z-index: 10;
}

/* 標頭 */
/* thead th {
  background-color: var(--second-bgcolor);
  position: sticky;
  top: 0;
  z-index: 5;
  white-space: nowrap;
} */

.merge {
  height: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-around;
}

.inner-example-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
  margin: 0;
}

.inner-example-table th,
.inner-example-table td {
  border: 1px solid #ccc;
  padding: 4px 6px;
  text-align: left;
  word-break: break-word;
}

.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.popup-content {
  background: white;
  padding: 20px;
  max-width: 400px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

/* 置中欄位用 */
.clickable-cell {
  text-align: center;
  vertical-align: middle;
}

/* 統一懸浮按鈕樣式 */
.popup-link {
  font-weight: bold;
  text-decoration: underline;
  cursor: pointer;
  display: inline-block;
  padding: 2px 6px;
  border-radius: 4px;
  transition: all 0.2s ease;
  font-size: 13px;
}

/* 不同顏色 */
.popup-link.blue {
  color: #1e40af;
  /* 藍色 */
}

.popup-link.green {
  color: #166534;
  /* 綠色 */
}

/* hover 效果 */
.popup-link:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
</style>
