<template>
  <div class="tag-container">
    <el-card shadow="hover">
      <div style="display:flex; justify-content:space-between; margin-bottom:20px">
        <el-button type="primary" @click="openAddDialog">新增标签</el-button>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="name" label="标签名称" width="200" />
        <el-table-column prop="material_count" label="关联素材数" width="120" />
        <el-table-column prop="created_at" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 新增/编辑标签弹窗 -->
    <el-dialog v-model="dialogVisible" title="标签管理" width="400px">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入标签名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTagList, addTag, editTag, delTag } from '@/api/tagCategory'

const loading = ref(false)
const page = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const tableData = ref<any[]>([])

// 弹窗相关
const dialogVisible = ref(false)
const formRef = ref()
const form = ref({ id: null as number | null, name: '' })
const rules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
}

// 加载标签列表
const loadList = async () => {
  loading.value = true
  try {
    const res = await getTagList()
    tableData.value = res.data
    page.total = res.data.length
  } catch (err) {
    console.error('加载标签列表失败', err)
  } finally {
    loading.value = false
  }
}

// 打开新增标签弹窗
const openAddDialog = () => {
  form.value = { id: null, name: '' }
  dialogVisible.value = true
}

// 打开编辑标签弹窗
const openEditDialog = (row: any) => {
  form.value = { id: row.id, name: row.name }
  dialogVisible.value = true
}

// 提交新增/编辑
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (form.value.id) {
      // 编辑标签
      await editTag(form.value.id, { name: form.value.name })
      ElMessage.success('编辑成功')
    } else {
      // 新增标签
      await addTag({ name: form.value.name })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (err) {
    console.error('提交失败', err)
  }
}

// 删除标签
const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该标签？关联素材将失去该标签', '提示', {
    type: 'warning'
  })
  try {
    await delTag(id)
    ElMessage.success('删除成功')
    loadList()
  } catch (err) {
    console.error('删除失败', err)
  }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.tag-container { padding: 0; }
</style>