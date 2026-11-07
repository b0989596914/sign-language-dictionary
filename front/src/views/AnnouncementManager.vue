<template>
    <div class="container mt-4" style="font-family: 'Noto Sans', sans-serif;">
        <h2 class="mb-4">公告管理系統</h2>

        <ul class="nav nav-tabs mb-4">
            <li class="nav-item">
                <button class="nav-link" :class="{ active: currentTab === 'news' }" @click="changeTab('news')">
                    最新消息管理
                </button>
            </li>
            <li class="nav-item">
                <button class="nav-link" :class="{ active: currentTab === 'faq' }" @click="changeTab('faq')">
                    常見問題管理
                </button>
            </li>
        </ul>

        <div class="content-container">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="fw-bold">{{ currentTab === 'news' ? '最新消息' : '常見問題' }}清單</h4>
                <button class="btn btn-primary btn-lg" @click="showAddForm = !showAddForm">
                    {{ showAddForm ? '取消新增' : '+ 快速新增' }}
                </button>
            </div>

            <transition name="fade">
                <div v-if="showAddForm" class="card card-body mb-4 shadow-sm border-primary">
                    <h5 class="mb-3 text-primary fw-bold">填寫新增內容</h5>

                    <div v-if="currentTab === 'news'">
                        <div class="mb-3">
                            <label class="form-label fw-bold">消息標題</label>
                            <input v-model="newNews.title" type="text" class="form-control form-control-lg"
                                placeholder="輸入標題">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">消息內容</label>
                            <textarea v-model="newNews.content" class="form-control form-control-lg" rows="3"
                                placeholder="輸入內容"></textarea>
                        </div>
                        <div class="form-check mb-3">
                            <input v-model="newNews.isPinned" class="form-check-input" type="checkbox" id="pinCheck">
                            <label class="form-check-label" for="pinCheck">是否置頂公告</label>
                        </div>
                        <button class="btn btn-success btn-lg w-100" @click="submitNews"
                            :disabled="!newNews.title">確認送出消息</button>
                    </div>

                    <div v-else>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label fw-bold">問題分類</label>
                                <input v-model="newFaq.category" type="text" class="form-control form-control-lg"
                                    placeholder="如：操作問題">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label fw-bold">排序權重 (數字越小越前面)</label>
                                <input v-model="newFaq.sortOrder" type="number" class="form-control form-control-lg">
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">問題題目 (Q)</label>
                            <input v-model="newFaq.question" type="text" class="form-control form-control-lg"
                                placeholder="輸入問題">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">回答內容 (A)</label>
                            <textarea v-model="newFaq.answer" class="form-control form-control-lg" rows="3"
                                placeholder="輸入回答"></textarea>
                        </div>
                        <button class="btn btn-success btn-lg w-100" @click="submitFaq"
                            :disabled="!newFaq.question">確認送出問題</button>
                    </div>
                </div>
            </transition>

            <div class="table-responsive">
                <table v-if="currentTab === 'news'" class="table table-hover border align-middle">
                    <thead class="table-light">
                        <tr>
                            <th style="width: 100px;">狀態</th>
                            <th>消息標題</th>
                            <th style="width: 180px;">發佈日期</th>
                            <th style="width: 150px;">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in newsList" :key="item.id">
                            <td>
                                <span v-if="item.isPinned" class="badge bg-danger">置頂</span>
                                <span v-else class="badge bg-secondary">一般</span>
                            </td>
                            <td class="text-start fw-bold">{{ item.title }}</td>
                            <td>{{ formatDate(item.publishDate) }}</td>
                            <td>
                                <button class="btn btn-sm btn-outline-danger" @click="deleteNews(item.id)">刪除</button>
                            </td>
                        </tr>
                        <tr v-if="newsList.length === 0">
                            <td colspan="4" class="py-4 text-muted">目前暫無消息資料</td>
                        </tr>
                    </tbody>
                </table>

                <table v-else class="table table-hover border align-middle">
                    <thead class="table-light">
                        <tr>
                            <th style="width: 150px;">分類</th>
                            <th>問題題目</th>
                            <th style="width: 100px;">排序</th>
                            <th style="width: 150px;">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in faqList" :key="item.id">
                            <td><span class="badge bg-info text-dark">{{ item.category }}</span></td>
                            <td class="text-start fw-bold">{{ item.question }}</td>
                            <td>{{ item.sortOrder }}</td>
                            <td>
                                <button class="btn btn-sm btn-outline-danger" @click="deleteFaq(item.id)">刪除</button>
                            </td>
                        </tr>
                        <tr v-if="faqList.length === 0">
                            <td colspan="4" class="py-4 text-muted">目前暫無常見問題</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<script>
import instance from '@/utils/axios.js';

export default {
    data() {
        return {
            currentTab: 'news',
            showAddForm: false,
            newsList: [],
            faqList: [],
            newNews: { title: '', content: '', isPinned: false },
            newFaq: { category: '', question: '', answer: '', sortOrder: 0 }
        };
    },
    methods: {
        // --- API 請求相關 ---
        async fetchNews() {
            try {
                const response = await instance.get('/announcement/news/list');
                this.newsList = response.data;
            } catch (error) {
                console.error("抓取消息失敗:", error);
            }
        },
        async fetchFaqs() {
            try {
                const response = await instance.get('/announcement/faq/list');
                this.faqList = response.data;
            } catch (error) {
                console.error("抓取 FAQ 失敗:", error);
            }
        },
        async submitNews() {
            try {
                await instance.post('/announcement/news/create', this.newNews);
                alert('消息發佈成功！');
                this.resetForm();
                this.fetchNews();
            } catch (error) {
                alert('發佈失敗，請檢查網路連接');
            }
        },
        async submitFaq() {
            try {
                await instance.post('/announcement/faq/save', this.newFaq);
                alert('問題新增成功！');
                this.resetForm();
                this.fetchFaqs();
            } catch (error) {
                alert('新增失敗');
            }
        },
        async deleteNews(id) {
            if (confirm("確定要刪除這條消息嗎？")) {
                try {
                    await instance.delete(`/announcement/news/delete/${id}`);
                    this.fetchNews();
                } catch (error) {
                    alert("刪除失敗");
                }
            }
        },
        async deleteFaq(id) {
            if (confirm("確定要刪除這個問題嗎？")) {
                try {
                    await instance.delete(`/announcement/faq/delete/${id}`);
                    this.fetchFaqs();
                } catch (error) {
                    alert("刪除失敗");
                }
            }
        },
        // --- 功能性方法 ---
        resetForm() {
            this.showAddForm = false;
            this.newNews = { title: '', content: '', isPinned: false };
            this.newFaq = { category: '', question: '', answer: '', sortOrder: 0 };
        },
        changeTab(tab) {
            this.currentTab = tab;
            this.showAddForm = false; // 切換時自動關閉表單
            if (tab === 'news') this.fetchNews();
            else this.fetchFaqs();
        },
        formatDate(dateStr) {
            if (!dateStr) return "";
            return dateStr.replace("T", " ").substring(0, 16);
        }
    },
    mounted() {
        this.fetchNews(); // 初始化載入消息
    }
};
</script>

<style scoped>
.nav-link {
    font-size: 18px;
    cursor: pointer;
    color: var(--main-textcolor, #333);
}

.nav-link.active {
    font-weight: bold;
    color: #0d6efd !important;
}

.form-control-lg,
.btn-lg {
    font-size: 17px !important;
}

.table th {
    font-size: 17px;
    background-color: #f8f9fa;
}

.table td {
    font-size: 17px;
}

.text-start {
    text-align: left !important;
}

/* 簡單的淡入淡出動畫 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s;
}

.fade-enter,
.fade-leave-to {
    opacity: 0;
}
</style>