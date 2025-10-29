<!--
  库存快照列表页
  
  功能说明：
  1. 库存快照的查询功能
  2. 支持按日期范围、仓库、商品查询
  3. 支持分页显示
  4. 权限控制：wms:inventory-snapshot:query
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true">
      <el-form-item label="快照日期" prop="snapshotDateRange">
        <el-date-picker
          v-model="queryParams.snapshotDateRange"
          value-format="YYYY-MM-DD"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
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
      
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="快照日期" prop="snapshotDate" width="120" fixed="left" />
      <el-table-column label="仓库" prop="warehouseName" width="120" />
      <el-table-column label="商品信息" min-width="200">
        <template #default="scope">
          <div>{{ scope.row.skuCode }}</div>
          <div class="text-gray-500 text-xs">{{ scope.row.goodsName }}</div>
        </template>
      </el-table-column>
      <el-table-column label="库存数量" prop="quantity" width="120" align="right">
        <template #default="scope">
          <span class="font-medium">{{ scope.row.quantity }}</span>
        </template>
      </el-table-column>
      <el-table-column label="锁定数量" prop="lockQuantity" width="120" align="right">
        <template #default="scope">
          <span v-if="scope.row.lockQuantity > 0" class="text-orange-500">{{ scope.row.lockQuantity }}</span>
          <span v-else class="text-gray-400">0</span>
        </template>
      </el-table-column>
      <el-table-column label="可用数量" prop="availableQuantity" width="120" align="right">
        <template #default="scope">
          <span class="text-green-600 font-medium">{{ scope.row.availableQuantity }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" :formatter="dateFormatter" />
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
import * as InventorySnapshotApi from '@/api/wms/inventory-snapshot'
import * as WarehouseApi from '@/api/wms/warehouse'
import * as GoodsApi from '@/api/wms/goods'
import { dateFormatter } from '@/utils/formatTime'

defineOptions({ name: 'WmsInventorySnapshot' })

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
  snapshotDateRange: undefined,
  warehouseId: undefined,
  goodsId: undefined
})
const queryFormRef = ref()

/**
 * 查询列表
 */
const getList = async () => {
  loading.value = true
  try {
    const data = await InventorySnapshotApi.getInventorySnapshotPage(queryParams)
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

