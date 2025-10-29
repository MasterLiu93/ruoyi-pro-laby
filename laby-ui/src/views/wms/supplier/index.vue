<!--
  供应商管理列表页
  
  功能说明：
  1. 供应商的查询、新增、编辑、删除
  2. 支持多条件搜索（供应商编码、名称、类型、信用等级、联系人、电话、状态）
  3. 支持分页显示
  4. 权限控制：wms:supplier:query、create、update、delete
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true">
      <el-form-item label="供应商编码" prop="supplierCode">
        <el-input
          v-model="queryParams.supplierCode"
          placeholder="请输入供应商编码"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="供应商名称" prop="supplierName">
        <el-input
          v-model="queryParams.supplierName"
          placeholder="请输入供应商名称"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="供应商类型" prop="supplierType">
        <el-select
          v-model="queryParams.supplierType"
          placeholder="请选择供应商类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_SUPPLIER_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="信用等级" prop="creditLevel">
        <el-select
          v-model="queryParams.creditLevel"
          placeholder="请选择信用等级"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_SUPPLIER_CREDIT_LEVEL)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input
          v-model="queryParams.contactPerson"
          placeholder="请输入联系人"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input
          v-model="queryParams.contactPhone"
          placeholder="请输入联系电话"
          clearable
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="datetimerange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" />
          重置
        </el-button>
        <el-button type="primary" @click="openForm('create')" v-hasPermi="['wms:supplier:create']">
          <Icon icon="ep:plus" class="mr-5px" />
          新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="供应商编码" prop="supplierCode" min-width="120" />
      <el-table-column label="供应商名称" prop="supplierName" min-width="180" show-overflow-tooltip />
      <el-table-column label="供应商类型" prop="supplierType" width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_SUPPLIER_TYPE" :value="scope.row.supplierType" />
        </template>
      </el-table-column>
      <el-table-column label="联系人" prop="contactPerson" width="100" show-overflow-tooltip />
      <el-table-column label="联系电话" prop="contactPhone" width="120" />
      <el-table-column label="省市区" min-width="150" show-overflow-tooltip>
        <template #default="scope">
          {{ [scope.row.province, scope.row.city, scope.row.district].filter(Boolean).join(' ') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="详细地址" prop="address" min-width="180" show-overflow-tooltip />
      <el-table-column label="信用等级" prop="creditLevel" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_SUPPLIER_CREDIT_LEVEL" :value="scope.row.creditLevel" />
        </template>
      </el-table-column>
      <el-table-column label="合作开始日期" prop="cooperationStartDate" width="120">
        <template #default="scope">
          {{ formatCooperationDate(scope.row.cooperationStartDate) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" :formatter="dateFormatter" />
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-hasPermi="['wms:supplier:update']">
            编辑
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['wms:supplier:delete']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗 -->
  <SupplierForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as SupplierApi from '@/api/wms/supplier'
import SupplierForm from './SupplierForm.vue'

/**
 * 供应商管理列表页组件定义
 */
defineOptions({ name: 'WmsSupplier' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true) // 列表加载中
const list = ref([]) // 列表数据
const total = ref(0) // 总条数

// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  supplierCode: undefined,
  supplierName: undefined,
  supplierType: undefined,
  creditLevel: undefined,
  contactPerson: undefined,
  contactPhone: undefined,
  status: undefined,
  createTime: undefined
})
const queryFormRef = ref()

/**
 * 查询列表
 */
const getList = async () => {
  loading.value = true
  try {
    const data = await SupplierApi.getSupplierPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/**
 * 搜索按钮操作
 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/**
 * 重置按钮操作
 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/**
 * 打开表单
 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/**
 * 删除操作
 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await SupplierApi.deleteSupplier(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/**
 * 日期格式化（创建时间）
 */
const dateFormatter = (row, column, cellValue) => {
  if (!cellValue) return '-'
  return formatDate(cellValue)
}

/**
 * 合作日期格式化（处理 LocalDate 数组格式）
 */
const formatCooperationDate = (date) => {
  if (!date) return '-'
  // 如果是数组格式 [2021, 1, 15]
  if (Array.isArray(date) && date.length === 3) {
    const [year, month, day] = date
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  }
  // 如果已经是字符串格式
  if (typeof date === 'string') {
    return date
  }
  return '-'
}

/**
 * 初始化
 */
onMounted(() => {
  getList()
})
</script>

