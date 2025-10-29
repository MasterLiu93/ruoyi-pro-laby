<!--
  库存报表页面
  
  功能说明：
  1. 库存汇总统计（总商品数、总库存、可用库存、锁定库存、低库存预警）
  2. 库存明细列表（支持多条件搜索）
  3. 支持导出Excel
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 汇总卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">商品种类</div>
            <div class="statistic-value">{{ summary.totalGoodsCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">总库存</div>
            <div class="statistic-value text-primary">{{ summary.totalQuantity || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">可用库存</div>
            <div class="statistic-value text-success">{{ summary.availableQuantity || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">锁定库存</div>
            <div class="statistic-value text-warning">{{ summary.lockedQuantity || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">低库存预警</div>
            <div class="statistic-value text-danger">{{ summary.lowStockCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">零库存</div>
            <div class="statistic-value text-info">{{ summary.zeroStockCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

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

      <el-form-item label="商品名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入商品名称" clearable class="!w-240px" />
      </el-form-item>

      <el-form-item label="SKU编码" prop="skuCode">
        <el-input v-model="queryParams.skuCode" placeholder="请输入SKU编码" clearable class="!w-240px" />
      </el-form-item>

      <el-form-item label="低库存" prop="lowStock">
        <el-checkbox v-model="queryParams.lowStock">仅显示低库存</el-checkbox>
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="仓库" prop="warehouseName" width="120" align="center" />
      <el-table-column label="库位" prop="locationCode" width="120" align="center" />
      <el-table-column label="SKU编码" prop="skuCode" width="140" align="center" />
      <el-table-column label="商品名称" prop="goodsName" min-width="180" show-overflow-tooltip />
      <el-table-column label="分类" prop="categoryName" width="120" align="center" />
      <el-table-column label="批次号" prop="batchNo" width="120" align="center" />
      <el-table-column label="总数量" prop="totalQuantity" width="100" align="center" />
      <el-table-column label="可用数量" prop="availableQuantity" width="100" align="center">
        <template #default="scope">
          <span :class="{ 'text-danger': scope.row.isLowStock }">{{ scope.row.availableQuantity }}</span>
        </template>
      </el-table-column>
      <el-table-column label="锁定数量" prop="lockedQuantity" width="100" align="center" />
      <el-table-column label="安全库存" prop="safetyStock" width="100" align="center" />
      <el-table-column label="库存状态" prop="stockStatus" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.isLowStock ? 'danger' : 'success'">
            {{ scope.row.stockStatus }}
          </el-tag>
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
</template>

<script setup lang="ts">
import * as InventoryReportApi from '@/api/wms/inventoryReport'
import * as WarehouseApi from '@/api/wms/warehouse'

defineOptions({ name: 'WmsInventoryReport' })

const loading = ref(true)
const list = ref([])
const total = ref(0)
const warehouseList = ref([])
const summary = ref({
  totalGoodsCount: 0,
  totalQuantity: 0,
  availableQuantity: 0,
  lockedQuantity: 0,
  lowStockCount: 0,
  zeroStockCount: 0
})

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  warehouseId: undefined,
  goodsName: undefined,
  skuCode: undefined,
  lowStock: false
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await InventoryReportApi.getInventoryReportPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const getSummary = async () => {
  const data = await InventoryReportApi.getInventoryReportSummary(queryParams)
  summary.value = data
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
  getSummary()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

onMounted(async () => {
  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
  await getList()
  await getSummary()
})
</script>

<style scoped lang="scss">
.statistic-card {
  text-align: center;
  padding: 10px 0;
}

.statistic-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.statistic-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.text-primary {
  color: #409eff !important;
}

.text-success {
  color: #67c23a !important;
}

.text-warning {
  color: #e6a23c !important;
}

.text-danger {
  color: #f56c6c !important;
}

.text-info {
  color: #909399 !important;
}
</style>

