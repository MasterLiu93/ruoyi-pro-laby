<!--
  移库管理列表页
  
  功能说明：
  1. 移库单的查询、新增、编辑、删除
  2. 移库单的执行、完成、取消操作
  3. 支持多条件搜索和分页显示
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true">
      <el-form-item label="移库单号" prop="moveNo">
        <el-input
          v-model="queryParams.moveNo"
          placeholder="请输入移库单号"
          clearable
          class="!w-240px"
        />
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

      <el-form-item label="移库类型" prop="moveType">
        <el-select v-model="queryParams.moveType" placeholder="请选择移库类型" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_STOCK_MOVE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="商品名称" prop="goodsName">
        <el-input
          v-model="queryParams.goodsName"
          placeholder="请输入商品名称"
          clearable
          class="!w-240px"
        />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_STOCK_MOVE_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="primary" @click="openForm('create')" v-hasPermi="['wms:stock-move:create']">
          新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="移库单号" prop="moveNo" width="160" align="center" />
      <el-table-column label="仓库" prop="warehouseName" width="120" align="center" />
      <el-table-column label="移库类型" prop="moveType" width="100" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_STOCK_MOVE_TYPE" :value="scope.row.moveType" />
        </template>
      </el-table-column>
      <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
      <el-table-column label="SKU编码" prop="skuCode" width="140" align="center" />
      <el-table-column label="批次号" prop="batchNo" width="120" align="center" />
      <el-table-column label="源库位" prop="fromLocationCode" width="120" align="center" />
      <el-table-column label="目标库位" prop="toLocationCode" width="120" align="center" />
      <el-table-column label="数量" prop="quantity" width="100" align="center" />
      <el-table-column label="移库原因" prop="moveReason" width="150" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="90" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_STOCK_MOVE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        prop="createTime"
        width="160"
        align="center"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" fixed="right" width="200" align="center">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['wms:stock-move:update']"
            v-if="scope.row.status === 1"
          >
            编辑
          </el-button>
          <el-button
            link
            type="primary"
            @click="handleExecute(scope.row.id)"
            v-hasPermi="['wms:stock-move:execute']"
            v-if="scope.row.status === 1"
          >
            执行
          </el-button>
          <el-button
            link
            type="success"
            @click="handleComplete(scope.row.id)"
            v-hasPermi="['wms:stock-move:complete']"
            v-if="scope.row.status === 2"
          >
            完成
          </el-button>
          <el-button
            link
            type="warning"
            @click="handleCancel(scope.row.id)"
            v-hasPermi="['wms:stock-move:cancel']"
            v-if="scope.row.status === 1 || scope.row.status === 2"
          >
            取消
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['wms:stock-move:delete']"
            v-if="scope.row.status === 1"
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
  <StockMoveForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import * as StockMoveApi from '@/api/wms/stockMove'
import * as WarehouseApi from '@/api/wms/warehouse'
import StockMoveForm from './StockMoveForm.vue'

/**
 * 移库管理列表页组件定义
 */
defineOptions({ name: 'WmsStockMove' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true) // 列表加载中
const list = ref([]) // 列表数据
const total = ref(0) // 总条数
const warehouseList = ref([]) // 仓库列表

// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  moveNo: undefined,
  warehouseId: undefined,
  moveType: undefined,
  goodsName: undefined,
  status: undefined
})
const queryFormRef = ref()

/**
 * 查询列表
 */
const getList = async () => {
  loading.value = true
  try {
    const data = await StockMoveApi.getStockMovePage(queryParams)
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
 * 执行移库
 */
const handleExecute = async (id: number) => {
  try {
    await message.confirm('确认执行该移库单吗？')
    await StockMoveApi.executeStockMove(id)
    message.success('执行成功')
    await getList()
  } catch {}
}

/**
 * 完成移库
 */
const handleComplete = async (id: number) => {
  try {
    await message.confirm('确认完成该移库单吗？')
    await StockMoveApi.completeStockMove(id)
    message.success('完成成功')
    await getList()
  } catch {}
}

/**
 * 取消移库
 */
const handleCancel = async (id: number) => {
  try {
    await message.confirm('确认取消该移库单吗？')
    await StockMoveApi.cancelStockMove(id)
    message.success('取消成功')
    await getList()
  } catch {}
}

/**
 * 删除操作
 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await StockMoveApi.deleteStockMove(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/**
 * 日期格式化
 */
const dateFormatter = (row, column, cellValue) => {
  if (!cellValue) return '-'
  return formatDate(cellValue)
}

/**
 * 初始化
 */
onMounted(async () => {
  await getList()
  // 加载仓库列表
  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
})
</script>

