<!--
  库存流水列表页
  
  功能说明：
  1. 库存流水的查询功能
  2. 支持多条件搜索（仓库、商品、批次、操作类型等）
  3. 支持分页显示
  4. 权限控制：wms:inventory-log:query
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true">
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
      
      <el-form-item label="商品" prop="goodsId">
        <el-select v-model="queryParams.goodsId" placeholder="请选择商品" clearable class="!w-240px" filterable>
          <el-option
            v-for="goods in goodsList"
            :key="goods.id"
            :label="`${goods.skuCode} - ${goods.goodsName}`"
            :value="goods.id"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="操作类型" prop="operationType">
        <el-select v-model="queryParams.operationType" placeholder="请选择操作类型" clearable>
          <el-option label="入库" value="INBOUND" />
          <el-option label="出库" value="OUTBOUND" />
          <el-option label="移库" value="MOVE" />
          <el-option label="锁定" value="LOCK" />
          <el-option label="解锁" value="UNLOCK" />
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
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="仓库" prop="warehouseName" width="120" />
      <el-table-column label="商品信息" min-width="200">
        <template #default="scope">
          <div>{{ scope.row.skuCode }}</div>
          <div class="text-gray-500 text-xs">{{ scope.row.goodsName }}</div>
        </template>
      </el-table-column>
      <el-table-column label="库位" prop="locationCode" width="120" />
      <el-table-column label="批次号" prop="batchNo" width="140" />
      <el-table-column label="操作类型" prop="operationType" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.operationType === 'INBOUND'" type="success">入库</el-tag>
          <el-tag v-else-if="scope.row.operationType === 'OUTBOUND'" type="danger">出库</el-tag>
          <el-tag v-else-if="scope.row.operationType === 'MOVE'" type="warning">移库</el-tag>
          <el-tag v-else-if="scope.row.operationType === 'LOCK'" type="info">锁定</el-tag>
          <el-tag v-else-if="scope.row.operationType === 'UNLOCK'">解锁</el-tag>
          <el-tag v-else>{{ scope.row.operationType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="变化数量" prop="quantityChange" width="100" align="right">
        <template #default="scope">
          <span :class="scope.row.quantityChange > 0 ? 'text-green-500' : 'text-red-500'">
            {{ scope.row.quantityChange > 0 ? '+' : '' }}{{ scope.row.quantityChange }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作前" prop="quantityBefore" width="100" align="right" />
      <el-table-column label="操作后" prop="quantityAfter" width="100" align="right" />
      <el-table-column label="业务类型" prop="businessType" width="100" />
      <el-table-column label="业务单号" prop="businessNo" width="150" show-overflow-tooltip />
      <el-table-column label="操作人" prop="operator" width="100" />
      <el-table-column label="操作时间" prop="createTime" width="160" :formatter="dateFormatter" />
      <el-table-column label="备注" prop="remark" min-width="150" show-overflow-tooltip />
    </el-table>
    
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts">
import * as InventoryLogApi from '@/api/wms/inventory-log'
import * as WarehouseApi from '@/api/wms/warehouse'
import * as GoodsApi from '@/api/wms/goods'
import { dateFormatter } from '@/utils/formatTime'

defineOptions({ name: 'WmsInventoryLog' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref([])
const total = ref(0)
const warehouseList = ref([])
const goodsList = ref([])

// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  warehouseId: undefined,
  goodsId: undefined,
  locationId: undefined,
  batchNo: undefined,
  operationType: undefined,
  businessType: undefined,
  businessNo: undefined,
  createTime: undefined
})
const queryFormRef = ref()

/**
 * 查询列表
 */
const getList = async () => {
  loading.value = true
  try {
    const data = await InventoryLogApi.getInventoryLogPage(queryParams)
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
 * 初始化
 */
onMounted(async () => {
  await getList()
  // 加载下拉数据
  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
  goodsList.value = await GoodsApi.getGoodsSimpleList()
})
</script>

