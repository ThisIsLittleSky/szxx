<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索标签名称"
          style="width: 240px; margin-right: 16px;"
          clearable
          @keyup.enter="getList"
          @clear="getList"
        />
        <el-button type="primary" @click="openAddDialog">新增标签</el-button>
      </div>

      <el-table
        :data="tableData"
        border
        style="width: 100%; margin: 20px 0;"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="标签名称" min-width="200" />
        <el-table-column prop="materialCount" label="关联素材数" width="120" align="center" />
        <el-table-column label="创建时间" width="180" align="center">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 新增/编辑标签弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑标签' : '新增标签'" width="420px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入标签名称" maxlength="32" show-word-limit />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTagList, addTag, editTag, delTag } from '@/api/tagCategory'
import { formatDate } from '@/utils/date'

const loading = ref(false)
const keyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)
const tableData = ref<any[]>([])

const dialogVisible = ref(false)
const formRef = ref()
const form = ref({ id: null as number | null, name: '' })
const rules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getTagList({ page: page.value, size: size.value, keyword: keyword.value || undefined })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    ElMessage.error('加载标签列表失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  form.value = { id: null, name: '' }
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  form.value = { id: row.id, name: row.name }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  try {
    if (form.value.id) {
      await editTag(form.value.id, { name: form.value.name })
      ElMessage.success('编辑成功')
    } else {
      await addTag({ name: form.value.name })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (err) {
    console.error('提交失败', err)
  }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该标签？关联素材将失去该标签', '提示', { type: 'warning' })
  try {
    await delTag(id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && page.value > 1) {
      page.value--
    }
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
