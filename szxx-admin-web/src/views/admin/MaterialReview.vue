<template>
  <div class="review-container">
    <el-card shadow="hover">
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="title" label="素材标题" min-width="200" />
        <el-table-column prop="category" label="文化品类" width="120" />
        <el-table-column prop="education_level" label="学段" width="120" />
        <el-table-column prop="uploader" label="上传人" width="120" />
        <el-table-column prop="created_at" label="上传时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleApprove(row)">通过</el-button>
            <el-button type="danger" size="small" @click="openRejectDialog(row)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="page.pageNum"
        v-model:page-size="page.pageSize"
        :total="page.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top:20px; text-align:right"
        @current-change="loadList"
        @size-change="loadList"
      />
    </el-card>

    <!-- 驳回原因弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="驳回素材" width="400px">
      <el-form :model="rejectForm">
        <el-form-item label="驳回原因">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleReject">确定驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminMaterialList, reviewMaterial } from '@/api/material'

const loading = ref(false)
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref<any[]>([])

// 驳回弹窗相关
const rejectDialogVisible = ref(false)
const rejectForm = ref({ materialId: 0, reason: '' })

// 加载待审核素材列表
const loadList = async () => {
  loading.value = true
  try {
    const res = await getAdminMaterialList({
      page: page.pageNum,
      size: page.pageSize,
      status: 'pending'
    })
    tableData.value = res.data.records
    page.total = res.data.total
  } catch (err) {
    console.error('加载待审核列表失败', err)
  } finally {
    loading.value = false
  }
}

// 通过素材审核
const handleApprove = async (row: any) => {
  try {
    await reviewMaterial(row.id, { status: 'approved', reason: '' })
    ElMessage.success('审核通过')
    loadList()
  } catch (err) {
    console.error('审核失败', err)
  }
}

// 打开驳回弹窗
const openRejectDialog = (row: any) => {
  rejectForm.value.materialId = row.id
  rejectForm.value.reason = ''
  rejectDialogVisible.value = true
}

// 执行驳回操作
const handleReject = async () => {
  if (!rejectForm.value.reason) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  try {
    await reviewMaterial(rejectForm.value.materialId, {
      status: 'rejected',
      reason: rejectForm.value.reason
    })
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    loadList()
  } catch (err) {
    console.error('驳回失败', err)
  }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.review-container { padding: 0; }
</style>