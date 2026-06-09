<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input
          v-model="searchKey"
          placeholder="搜索用户名/昵称"
          style="width: 220px; margin-right: 16px;"
          clearable
          @keyup.enter="getList"
        />
        <el-select v-model="role" placeholder="用户角色" style="width: 120px; margin-right: 16px;" @change="getList">
          <el-option label="全部" value="" />
          <el-option label="管理员" value="admin" />
          <el-option label="教师" value="teacher" />
          <el-option label="学生" value="student" />
        </el-select>
        <el-select v-model="userStatus" placeholder="账号状态" style="width: 120px;" @change="getList">
          <el-option label="全部" value="" />
          <el-option label="正常" value="active" />
          <el-option label="禁用" value="disabled" />
        </el-select>
      </div>

      <el-table 
        :data="tableData" 
        border 
        style="width: 100%; margin: 20px 0;"
        v-loading="tableLoading"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <span>{{ row.role === 'admin' ? '管理员' : row.role === 'teacher' ? '教师' : '学生' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="账号状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="注册时间" width="180" align="center" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 编辑用户弹窗 -->
    <el-dialog v-model="editVisible" title="编辑用户" width="400px">
      <el-form label-width="80px">
        <el-form-item label="用户角色">
          <el-select v-model="editRole" style="width:100%">
            <el-option label="管理员" value="admin" />
            <el-option label="教师" value="teacher" />
            <el-option label="学生" value="student" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号状态">
          <el-select v-model="editStatus" style="width:100%">
            <el-option label="正常" value="active" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminUserList, updateUserStatusRole, AdminUserItem } from '@/api/user'

// 筛选条件
const searchKey = ref('')
const role = ref('')
const userStatus = ref('')

// 分页
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 表格
const tableData = ref<AdminUserItem[]>([])
const tableLoading = ref(false)

// 编辑弹窗
const editVisible = ref(false)
const editLoading = ref(false)
const currentUserId = ref(0)
const editRole = ref('')
const editStatus = ref('')

// 获取用户列表
const getList = async () => {
  tableLoading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: searchKey.value,
      role: role.value,
      status: userStatus.value
    }
    const res = await getAdminUserList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    ElMessage.error('获取用户列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 打开编辑弹窗
const openEditDialog = (row: AdminUserItem) => {
  currentUserId.value = row.id
  editRole.value = row.role
  editStatus.value = row.status
  editVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  editLoading.value = true
  try {
    await updateUserStatusRole(currentUserId.value, {
      role: editRole.value,
      status: editStatus.value
    })
    ElMessage.success('修改成功')
    editVisible.value = false
    getList()
  } catch (err) {
    ElMessage.error('修改失败')
  } finally {
    editLoading.value = false
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