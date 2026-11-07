<template>
    <div class="container mt-4" style="max-width: 60%;">
      <h3 class="mb-3">分類管理</h3>
      <ul class="list-group">
        <!-- 🔼 新增分類輸入區 -->
        <div class="input-group mb-3">
        <input type="text" class="form-control" placeholder="輸入新分類名稱" v-model="newCategoryName">
        <button class="btn btn-primary" @click="addCategory">新增分類</button>
        </div>
        <li class="list-group-item d-flex justify-content-between align-items-center"
            v-for="cat in categoryOptions" :key="cat.value">
            <div v-if="editId !== cat.value">
                {{ cat.label }}
            </div>
            <input v-else v-model="editText" class="form-control me-2" style="width: 50%;" />
  
          
            <div class="d-flex justify-content-end" style="min-width: 150px;">
                <button v-if="editId !== cat.value" @click="startEdit(cat)" class="btn btn-sm btn-warning me-1">編輯</button>
                <button v-else @click="confirmEdit(cat)" class="btn btn-sm btn-success me-1">儲存</button>
                <button v-if="editId === cat.value" @click="cancelEdit" class="btn btn-sm btn-secondary me-1">取消</button>
                
                <button v-if="cat.value !== 0" @click="deleteCategory(cat)" class="btn btn-sm btn-danger">刪除</button>
                <button v-else disabled class="btn btn-sm btn-secondary" title="此分類不可刪除">刪除</button>
            </div>
        </li>
      </ul>
    </div>
  </template>
  
  

<script>
// import axios from 'axios';
// import axios from '@/utils/axios'

import instance from '@/utils/axios.js';
// const baseUrl = process.env.VUE_APP_API_URL
export default {
  data() {
    return {
        categoryOptions: [],
        newCategoryName: '',
        editId: null,
        editText: ''
    }
  },
  computed: {
    
  },
  created() {
    this.fetchCategories();

  },
  methods: {
    fetchCategories() {
      instance.get( '/Category/fetch').then(res => {
        this.categoryOptions = res.data.map(cat => ({
          value: cat.id,
          label: cat.categoryName
        }));
      });
    },
    startEdit(cat) {
      this.editId = cat.value;
      this.editText = cat.label;
    },
    cancelEdit() {
      this.editId = null;
      this.editText = '';
    },
    confirmEdit(cat) {
      instance.post( '/Category/update', {
        id: cat.value,
        newName: this.editText
      }).then(() => {
        this.fetchCategories();
        this.cancelEdit();
        alert('修改成功');
      }).catch(() => {
        alert('修改失敗');
      });
    },
    deleteCategory(cat) {
      if (confirm(`確定要刪除分類「${cat.label}」嗎？`)) {
        instance.delete( `/Category/delete/${cat.value}`).then(() => {
          this.fetchCategories();
          alert('刪除成功');
        }).catch(() => {
          alert('刪除失敗，請確認該分類是否被使用');
        });
      }
    },
    addCategory() {
        if (!this.newCategoryName.trim()) {
            alert('請輸入分類名稱');
            return;
        }

        instance.post('/Category/create', {
            name: this.newCategoryName.trim()
        }).then(() => {
            this.newCategoryName = '';
            this.fetchCategories(); // 重新讀取分類
            alert('新增成功');
        }).catch(() => {
            alert('新增失敗，可能是名稱重複');
        });
    },
  }
}
</script>

<style>
:root {
  --main-textcolor: #18112e;
  --second-bgcolor: #e9f1d6;
  --white: #ffffff;
}

</style>
