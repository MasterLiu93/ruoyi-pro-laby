<!--
  盘点计划列表页
  
  功能说明：
  1. 盘点计划的查询、新增、编辑、删除
  2. 盘点计划的审核、取消操作
  3. 支持多条件搜索和分页显示
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true">
      <el-form-item label="计划编号" prop="planNo">
        <el-input v-model="queryParams.planNo" placeholder="请输入计划编号" clearable class="!w-240px" />
      </el-form-item>

      <el-form-item label="计划名称" prop="planName">
        <el-input v-model="queryParams.planName" placeholder="请输入计划名称" clearable class="!w-240px" />
      </el-form-item>

      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="queryParams.warehouseId" placeholder="请选择仓库" clearable class="!w-240px">
          <el-option
            v-for="warehouse in warehouseList"
            :key="warehouse.id"
            :label="warehouse.warehouseName"
            :value="warehouse.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="盘点类型" prop="takingType">
        <el-select v-model="queryParams.takingType" placeholder="请选择盘点类型" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_STOCK_TAKING_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_STOCK_TAKING_PLAN_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="primary" @click="openForm('create')" v-hasPermi="['wms:stocktaking-plan:create']">
          新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="计划编号" prop="planNo" width="160" align="center" />
      <el-table-column label="计划名称" prop="planName" min-width="180" show-overflow-tooltip />
      <el-table-column label="仓库" prop="warehouseName" width="120" align="center" />
      <el-table-column label="盘点类型" prop="takingType" width="100" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_STOCK_TAKING_TYPE" :value="scope.row.takingType" />
        </template>
      </el-table-column>
      <el-table-column label="范围类型" prop="scopeType" width="100" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_STOCK_TAKING_SCOPE_TYPE" :value="scope.row.scopeType" />
        </template>
      </el-table-column>
      <el-table-column label="计划时间" width="180" align="center">
        <template #default="scope">
          <div>{{ formatDate(scope.row.planStartTime) }}</div>
          <div>至 {{ formatDate(scope.row.planEndTime) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="盘点进度" width="120" align="center">
        <template #default="scope">
          {{ scope.row.completedCount || 0 }} / {{ scope.row.totalCount || 0 }}
        </template>
      </el-table-column>
      <el-table-column label="差异数" prop="diffCount" width="80" align="center" />
      <el-table-column label="状态" prop="status" width="90" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_STOCK_TAKING_PLAN_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" :formatter="dateFormatter" />
      <el-table-column label="操作" fixed="right" width="200" align="center">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-hasPermi="['wms:stocktaking-plan:update']" v-if="scope.row.status === 1">
            编辑
          </el-button>
          <el-button link type="success" @click="handleAudit(scope.row.id)" v-hasPermi="['wms:stocktaking-plan:audit']" v-if="scope.row.status === 1">
            审核
          </el-button>
          <el-button link type="warning" @click="handleCancel(scope.row.id)" v-hasPermi="['wms:stocktaking-plan:cancel']" v-if="scope.row.status === 1 || scope.row.status === 2 || scope.row.status === 3">
            取消
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['wms:stocktaking-plan:delete']" v-if="scope.row.status === 1">
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
  <StockTakingPlanForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as StockTakingPlanApi from '@/api/wms/stockTakingPlan'
import * as WarehouseApi from '@/api/wms/warehouse'
import StockTakingPlanForm from './StockTakingPlanForm.vue'

defineOptions({ name: 'WmsStocktakingPlan' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref([])
const total = ref(0)
const warehouseList = ref([])

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  planNo: undefined,
  planName: undefined,
  warehouseId: undefined,
  takingType: undefined,
  status: undefined
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await StockTakingPlanApi.getStockTakingPlanPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleAudit = async (id: number) => {
  try {
    await message.confirm('确认审核该盘点计划吗？')
    await StockTakingPlanApi.auditStockTakingPlan(id)
    message.success('审核成功')
    await getList()
  } catch {}
}

const handleCancel = async (id: number) => {
  try {
    await message.confirm('确认取消该盘点计划吗？')
    await StockTakingPlanApi.cancelStockTakingPlan(id)
    message.success('取消成功')
    await getList()
  } catch {}
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await StockTakingPlanApi.deleteStockTakingPlan(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const dateFormatter = (row, column, cellValue) => {
  if (!cellValue) return '-'
  return formatDate(cellValue)
}

onMounted(async () => {
  await getList()
  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
})
</script>

