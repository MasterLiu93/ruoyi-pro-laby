<!--
  承运商信息列表页
  
  功能说明：
  1. 承运商信息的查询、新增、编辑、删除
  2. 支持多条件搜索（承运商编码、名称、类型、状态）
  3. 支持分页显示
  4. 权限控制：wms:carrier:query、create、update、delete
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true">
      <el-form-item label="承运商编码" prop="carrierCode">
        <el-input
          v-model="queryParams.carrierCode"
          placeholder="请输入承运商编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      
      <el-form-item label="承运商名称" prop="carrierName">
        <el-input
          v-model="queryParams.carrierName"
          placeholder="请输入承运商名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      
      <el-form-item label="承运商类型" prop="carrierType">
        <el-select
          v-model="queryParams.carrierType"
          placeholder="请选择承运商类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_CARRIER_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
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
          type="daterange"
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
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['wms:carrier:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" />
          新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="承运商编码" prop="carrierCode" width="140" show-overflow-tooltip />
      <el-table-column label="承运商名称" prop="carrierName" min-width="160" show-overflow-tooltip />
      
      <el-table-column label="承运商类型" prop="carrierType" width="110" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_CARRIER_TYPE" :value="scope.row.carrierType" />
        </template>
      </el-table-column>
      
      <el-table-column label="联系人" prop="contactPerson" width="100" show-overflow-tooltip />
      <el-table-column label="联系电话" prop="contactPhone" width="130" show-overflow-tooltip />
      
      <el-table-column label="服务区域" prop="serviceArea" min-width="150" show-overflow-tooltip />
      <el-table-column label="时效要求" prop="timeLimit" min-width="160" show-overflow-tooltip />
      
      <el-table-column label="服务评分" prop="rating" width="100" align="center">
        <template #default="scope">
          <el-rate
            v-model="scope.row.rating"
            disabled
            show-score
            text-color="#ff9900"
            score-template="{value}"
          />
        </template>
      </el-table-column>
      
      <el-table-column label="合作日期" prop="cooperationStartDate" width="110" align="center">
        <template #default="scope">
          {{ formatCooperationDate(scope.row.cooperationStartDate) }}
        </template>
      </el-table-column>
      
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      
      <el-table-column
        label="创建时间"
        prop="createTime"
        width="160"
        align="center"
        :formatter="dateFormatter"
      />
      
      <el-table-column label="操作" fixed="right" width="160" align="center">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['wms:carrier:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['wms:carrier:delete']"
          >
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
  <CarrierForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as CarrierApi from '@/api/wms/carrier'
import CarrierForm from './CarrierForm.vue'

defineOptions({ name: 'WmsCarrier' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref([])
const total = ref(0)

// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  carrierCode: undefined,
  carrierName: undefined,
  carrierType: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref()

/**
 * 查询列表
 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CarrierApi.getCarrierPage(queryParams)
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
    await CarrierApi.deleteCarrier(id)
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

