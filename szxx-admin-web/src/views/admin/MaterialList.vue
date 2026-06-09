<template>
  <div class="page-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKey"
          placeholder="搜索素材标题"
          style="width: 220px; margin-right: 16px;"
          clearable
          @keyup.enter="getList"
        />
        <!-- 改动1：全部选项 value 改为 undefined，不再传空字符串 -->
        <el-select v-model="status" placeholder="审核状态" style="width: 140px; margin-right: 16px;" @change="getList">
          <el-option label="全部" :value="undefined" />
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
      </div>

      <!-- 素材表格 -->
      <el-table
        :data="tableData"
        border
        style="width: 100%; margin: 20px 0;"
        v-loading="tableLoading"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="素材标题" />
        <el-table-column prop="dynasty" label="朝代" width="120" align="center" />
        <el-table-column prop="category" label="分类" width="120" align="center" />
        <el-table-column prop="status" label="审核状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'pending' ? 'warning' : 'danger'">
              {{ row.status === 'pending' ? '待审核' : row.status === 'approved' ? '已通过' : '已驳回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openReviewDialog(row)">审核</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页（参数改为 page / size） -->
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 审核弹窗 -->
    <el-dialog v-model="reviewVisible" title="素材审核" width="450px">
      <el-radio-group v-model="reviewStatus">
        <el-radio label="approved">通过审核</el-radio>
        <el-radio label="rejected">驳回审核</el-radio>
      </el-radio-group>
      <el-input
        v-model="reviewComment"
        type="textarea"
        rows="3"
        placeholder="请输入审核意见"
        style="margin-top: 16px;"
      />
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewLoading" @click="submitReview">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminMaterialList,
  deleteMaterial,
  reviewMaterial,
  AdminMaterialItem
} from '@/api/material'

// 搜索&筛选
const searchKey = ref('')
// 改动2：初始值改为 undefined，和「全部」选项保持一致
const status = ref< string | undefined >(undefined)

// 分页（后端标准：page / size）
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 表格数据
const tableData = ref<AdminMaterialItem[]>([])
const tableLoading = ref(false)

// 审核弹窗
const reviewVisible = ref(false)
const reviewLoading = ref(false)
const currentMaterialId = ref(0)
const reviewStatus = ref('approved')
const reviewComment = ref('')

// 获取素材列表
const getList = async () => {
  tableLoading.value = true
  try {
    // 改动3：动态组装参数，空状态不传给后端
    const params: any = {
      page: page.value,
      size: size.value,
      keyword: searchKey.value
    }
    // 只有状态有值时，才追加 status 参数
    if (status.value) {
      params.status = status.value
    }

    const res = await getAdminMaterialList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    ElMessage.error('获取素材列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 打开审核弹窗
const openReviewDialog = (row: AdminMaterialItem) => {
  currentMaterialId.value = row.id
  reviewStatus.value = row.status === 'approved' ? 'approved' : 'pending'
  reviewComment.value = ''
  reviewVisible.value = true
}

// 提交审核
const submitReview = async () => {
  reviewLoading.value = true
  try {
    await reviewMaterial(currentMaterialId.value, {
      status: reviewStatus.value,
      review_comment: reviewComment.value
    })
    ElMessage.success('审核操作成功')
    reviewVisible.value = false
    getList()
  } catch (err) {
    ElMessage.error('审核失败')
  } finally {
    reviewLoading.value = false
  }
}

// 删除素材
const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该素材？', '提示', { type: 'warning' })
  try {
    await deleteMaterial(id)
    ElMessage.success('删除成功')
    getList()
  } catch (err) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.page-container {
  background: #fff;
  border-radius: 8px;
}
.search-bar {
  display: flex;
  align-items: center;
}
</style>