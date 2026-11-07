<template>
    <div class="page-container">
        <div class="container py-5">

            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold text-dark mb-1">
                        <i class="bi bi-clipboard-data me-2"></i>詞彙錯誤回報
                    </h3>
                    <p class="text-muted mb-0">查看並管理使用者回報的詞彙問題</p>
                </div>
                <button class="btn btn-primary btn-lg shadow-sm d-flex align-items-center gap-2" @click="fetchReports"
                    :disabled="isLoading">
                    <span v-if="isLoading" class="spinner-border spinner-border-sm" role="status"
                        aria-hidden="true"></span>
                    <span v-else>🔄</span>
                    重新整理
                </button>
            </div>

            <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="bg-light">
                                <tr>
                                    <th class="py-3 ps-4 text-secondary text-uppercase fs-7 fw-bold" style="width: 8%">
                                        順序</th>

                                    <th class="py-3 text-secondary text-uppercase fs-7 fw-bold text-center"
                                        style="width: 25%">詞彙名稱</th>

                                    <th class="py-3 text-secondary text-uppercase fs-7 fw-bold text-start"
                                        style="width: 15%">錯誤類型</th>

                                    <th class="py-3 text-secondary text-uppercase fs-7 fw-bold text-start">詳細說明</th>

                                    <th class="py-3 text-secondary text-uppercase fs-7 fw-bold text-center"
                                        style="width: 15%">狀態</th>

                                    <th class="py-3 pe-4 text-secondary text-uppercase fs-7 fw-bold text-end"
                                        style="width: 15%">回報時間</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="report in reports" :key="report.id" class="fade-in-row">

                                    <td class="ps-4 fw-bold text-muted">{{ report.id }}</td>

                                    <td class="text-center">
                                        <div class="d-flex align-items-center justify-content-center gap-2">

                                            <a :href="`/dict/word/${report.wordId}`" target="_blank"
                                                class="id-badge text-decoration-none shadow-sm d-inline-flex align-items-center"
                                                title="查看前台頁面">

                                                <span class="me-1">{{ report.wordName || report.wordId }}</span>

                                                <span v-if="report.wordName" class="text-muted opacity-75 small ms-1"
                                                    style="font-size: 0.8em; font-weight: normal;">
                                                    ({{ report.wordId }})
                                                </span>

                                                <i
                                                    class="bi bi-box-arrow-up-right ms-2 small-icon text-muted opacity-75"></i>
                                            </a>

                                            <router-link :to="`/Edit/${report.wordId}`"
                                                class="btn btn-sm btn-outline-primary d-flex align-items-center gap-1 px-3 shadow-sm rounded-pill"
                                                title="編輯詞彙">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14"
                                                    fill="currentColor" viewBox="0 0 16 16">
                                                    <path
                                                        d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10zM11.207 2.5 13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293l6.5-6.5zm-9.761 5.175-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325z" />
                                                </svg>
                                                <span style="font-weight: 600; font-size: 0.9rem;">編輯</span>
                                            </router-link>

                                        </div>
                                    </td>

                                    <td class="text-start">
                                        <div class="d-flex flex-wrap gap-2">
                                            <span v-for="(type, index) in formatTags(report.errorTypes)" :key="index"
                                                class="badge rounded-pill bg-soft-danger text-danger border border-danger-subtle px-3 py-2"
                                                style="font-size: 0.95rem; letter-spacing: 1px;">
                                                {{ type }}
                                            </span>
                                        </div>
                                    </td>

                                    <td class="text-start">
                                        <div class="description-text text-dark">{{ report.description || '（無詳細說明）' }}
                                        </div>
                                    </td>

                                    <td class="text-center">
                                        <span :class="getStatusClass(report.status)"
                                            style="font-size: 0.95rem; letter-spacing: 1px;">
                                            {{ report.status === 'PENDING' ? '待處理' : report.status }}
                                        </span>
                                    </td>

                                    <td class="pe-4 text-end text-muted small">
                                        {{ formatDate(report.createdAt) }}
                                    </td>
                                </tr>

                                <tr v-if="reports.length === 0 && !isLoading">
                                    <td colspan="6" class="text-center py-5">
                                        <div class="py-4 text-muted">
                                            <div class="fs-1 mb-2">📭</div>
                                            <p class="mb-0">目前沒有任何回報資料</p>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="card-footer bg-white border-top py-3 px-4 text-muted small">
                    共 {{ reports.length }} 筆資料
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import instance from '@/utils/axios.js';

export default {
    name: "ReportList",
    data() {
        return {
            reports: [],
            isLoading: false
        };
    },
    created() {
        this.fetchReports();
    },
    methods: {
        fetchReports() {
            this.isLoading = true;
            instance.get('/word-report/list')
                .then(res => {
                    this.reports = res.data;
                    // 已移除名稱查詢邏輯
                })
                .catch(err => {
                    console.error(err);
                    alert('無法取得資料，請確認後端是否啟動');
                })
                .finally(() => {
                    this.isLoading = false;
                });
        },
        formatTags(typesString) {
            if (!typesString) return [];
            return typesString.split(',').filter(t => t.trim() !== '');
        },
        formatDate(dateString) {
            if (!dateString) return '-';
            const date = new Date(dateString);
            return date.toLocaleString('zh-TW', {
                year: 'numeric', month: '2-digit', day: '2-digit',
                hour: '2-digit', minute: '2-digit'
            });
        },
        getStatusClass(status) {
            // 這裡只負責顏色，padding 和尺寸統一在 template 設定 (px-3 py-2)
            if (status === 'PENDING') return 'badge rounded-pill bg-soft-warning text-warning border border-warning-subtle';
            if (status === 'RESOLVED') return 'badge rounded-pill bg-soft-success text-success border border-success-subtle';
            return 'badge rounded-pill bg-secondary';
        }
    }
};
</script>

<style scoped>
/* 頁面背景 */
.page-container {
    background-color: #f8f9fa;
    min-height: 100vh;
}

.fs-7 {
    font-size: 0.85rem;
    letter-spacing: 0.5px;
}

/* ID 連結樣式優化 */
.id-badge {
    color: #4361ee;
    font-family: 'Consolas', 'Monaco', monospace;
    font-weight: 600;
    padding: 6px 10px;
    background-color: #eff3ff;
    border-radius: 8px;
    transition: all 0.2s ease;
    border: 1px solid transparent;
    display: inline-block;
}

.id-badge:hover {
    background-color: #4361ee;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 6px rgba(67, 97, 238, 0.2);
}

.small-icon {
    font-size: 0.75rem;
}

/* 編輯按鈕互動特效 */
.edit-btn {
    transition: all 0.2s ease;
}

.edit-btn:hover {
    background-color: #4361ee !important;
    color: white !important;
    border-color: #4361ee !important;
    transform: translateY(-2px);
}

/* 柔和色系 */
.bg-soft-danger {
    background-color: #ffe5e5;
}

.text-danger {
    color: #d63384 !important;
}

.bg-soft-warning {
    background-color: #fff8e1;
}

.text-warning {
    color: #b76e00 !important;
}

.bg-soft-success {
    background-color: #d1e7dd;
}

.text-success {
    color: #0f5132 !important;
}

/* 文字樣式 */
.description-text {
    max-width: 400px;
    white-space: pre-wrap;
    line-height: 1.6;
    font-size: 0.95rem;
    text-align: left;
}

.shadow-sm {
    box-shadow: 0 .125rem .25rem rgba(0, 0, 0, .075) !important;
}

/* 進場動畫 */
.fade-in-row {
    animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(10px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>