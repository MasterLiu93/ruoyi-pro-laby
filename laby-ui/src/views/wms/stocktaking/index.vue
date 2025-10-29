<!--
  盘点单列表页
  
  功能说明：
  1. 盘点单的查询、新增、编辑、删除
  2. 盘点单的提交盘点、复核、调整库存操作
  3. 支持多条件搜索和分页显示
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true">
      <el-form-item label="盘点单号" prop="takingNo">
        <el-input v-model="queryParams.takingNo" placeholder="请输入盘点单号" clearable class="!w-240px" />
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

      <el-form-item label="商品名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入商品名称" clearable class="!w-240px" />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_STOCK_TAKING_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="primary" @click="openForm('create')" v-hasPermi="['wms:stocktaking:create']">
          新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="盘点单号" prop="takingNo" width="160" align="center" />
      <el-table-column label="计划编号" prop="planNo" width="160" align="center" />
      <el-table-column label="仓库" prop="warehouseName" width="120" align="center" />
      <el-table-column label="库位编码" prop="locationCode" width="120" align="center" />
      <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
      <el-table-column label="SKU编码" prop="skuCode" width="140" align="center" />
      <el-table-column label="批次号" prop="batchNo" width="120" align="center" />
      <el-table-column label="账面数量" prop="bookQuantity" width="100" align="center" />
      <el-table-column label="实盘数量" prop="actualQuantity" width="100" align="center" />
      <el-table-column label="差异数量" prop="diffQuantity" width="100" align="center">
        <template #default="scope">
          <span :class="{ 'text-red-500': scope.row.diffQuantity && scope.row.diffQuantity < 0, 'text-green-500': scope.row.diffQuantity && scope.row.diffQuantity > 0 }">
            {{ scope.row.diffQuantity || '-' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="90" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_STOCK_TAKING_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="盘点人" prop="operator" width="100" align="center" />
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" :formatter="dateFormatter" />
      <el-table-column label="操作" fixed="right" width="220" align="center">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-if="scope.row.status === 1">
            编辑
          </el-button>
          <el-button link type="success" @click="handleSubmit(scope.row)" v-if="scope.row.status === 1">
            提交盘点
          </el-button>
          <el-button link type="warning" @click="handleReview(scope.row.id)" v-if="scope.row.status === 2">
            复核
          </el-button>
          <el-button link type="primary" @click="handleAdjust(scope.row.id)" v-if="scope.row.status === 3">
            调整库存
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-if="scope.row.status === 1">
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
  <StockTakingForm ref="formRef" @success="getList" />

  <!-- 提交盘点弹窗 -->
  <Dialog title="提交盘点" v-model="submitDialogVisible" width="500px">
    <el-form :model="submitForm" label-width="100px">
      <el-form-item label="账面数量">
        <el-input v-model="submitForm.bookQuantity" disabled />
      </el-form-item>
      <el-form-item label="实盘数量">
        <el-input-number v-model="submitForm.actualQuantity" :min="0" :precision="2" class="!w-full" />
      </el-form-item>
      <el-form-item label="差异原因">
        <el-input v-model="submitForm.diffReason" type="textarea" :rows="3" placeholder="请输入差异原因" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="confirmSubmit" type="primary">确 定</el-button>
      <el-button @click="submitDialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as StockTakingApi from '@/api/wms/stockTaking'
import * as WarehouseApi from '@/api/wms/warehouse'
import StockTakingForm from './StockTakingForm.vue'

defineOptions({ name: 'WmsStocktaking' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref([])
const total = ref(0)
const warehouseList = ref([])
const submitDialogVisible = ref(false)
const submitForm = ref({
  id: undefined,
  bookQuantity: 0,
  actualQuantity: 0,
  diffReason: ''
})

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  takingNo: undefined,
  warehouseId: undefined,
  goodsName: undefined,
  status: undefined
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await StockTakingApi.getStockTakingPage(queryParams)
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

const handleSubmit = (row) => {
  submitForm.value = {
    id: row.id,
    bookQuantity: row.bookQuantity,
    actualQuantity: row.bookQuantity,
    diffReason: ''
  }
  submitDialogVisible.value = true
}

const confirmSubmit = async () => {
  try {
    await StockTakingApi.submitStockTaking(submitForm.value.id, submitForm.value.actualQuantity, submitForm.value.diffReason)
    message.success('提交成功')
    submitDialogVisible.value = false
    await getList()
  } catch {}
}

const handleReview = async (id: number) => {
  try {
    await message.confirm('确认复核该盘点单吗？')
    await StockTakingApi.reviewStockTaking(id)
    message.success('复核成功')
    await getList()
  } catch {}
}

const handleAdjust = async (id: number) => {
  try {
    await message.confirm('确认调整库存吗？调整后将更新实际库存。')
    await StockTakingApi.adjustStockTaking(id)
    message.success('调整成功')
    await getList()
  } catch {}
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await StockTakingApi.deleteStockTaking(id)
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

