<!--
  库存管理列表页
  
  功能说明：
  1. 库存的查询、新增、编辑、删除
  2. 支持多条件搜索（仓库、库位、商品、批次、序列号、状态等）
  3. 显示可用数量（库存数量-锁定数量）
  4. 关联显示仓库名、库位编码、商品名等信息
  5. 权限控制：wms:inventory:query、create、update、delete
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="88px"
    >
      <!-- 仓库下拉框 -->
      <el-form-item label="仓库" prop="warehouseId">
        <el-select
          v-model="queryParams.warehouseId"
          placeholder="请选择仓库"
          clearable
          class="!w-240px"
          @change="handleWarehouseChange"
        >
          <el-option
            v-for="warehouse in warehouseList"
            :key="warehouse.id"
            :label="warehouse.warehouseName"
            :value="warehouse.id"
          />
        </el-select>
      </el-form-item>
      
      <!-- 库位下拉框（联动） -->
      <el-form-item label="库位" prop="locationId">
        <el-select
          v-model="queryParams.locationId"
          placeholder="请选择库位"
          clearable
          class="!w-240px"
          :disabled="!queryParams.warehouseId"
        >
          <el-option
            v-for="location in locationList"
            :key="location.id"
            :label="location.locationCode"
            :value="location.id"
          />
        </el-select>
      </el-form-item>
      
      <!-- 商品名称搜索 -->
      <el-form-item label="商品名称" prop="goodsName">
        <el-input
          v-model="queryParams.goodsName"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      
      <!-- SKU编码搜索 -->
      <el-form-item label="SKU编码" prop="skuCode">
        <el-input
          v-model="queryParams.skuCode"
          placeholder="请输入SKU编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      
      <!-- 批次号搜索 -->
      <el-form-item label="批次号" prop="batchNo">
        <el-input
          v-model="queryParams.batchNo"
          placeholder="请输入批次号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      
      <!-- 状态字典下拉框 -->
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_INVENTORY_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      
      <!-- 操作按钮 -->
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['wms:inventory:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="仓库" align="center" prop="warehouseName" width="120px" />
      <el-table-column label="库位编码" align="center" prop="locationCode" width="120px">
        <template #default="scope">
          {{ scope.row.locationCode || '-' }}
        </template>
      </el-table-column>
      
      <el-table-column label="商品信息" align="center" min-width="200px">
        <template #default="scope">
          <div class="text-left">
            <div class="font-bold">{{ scope.row.goodsName }}</div>
            <div class="text-gray-400 text-sm">SKU: {{ scope.row.skuCode }}</div>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column label="批次/序列号" align="center" width="150px">
        <template #default="scope">
          <div v-if="scope.row.batchNo || scope.row.serialNo">
            <div v-if="scope.row.batchNo">批次: {{ scope.row.batchNo }}</div>
            <div v-if="scope.row.serialNo">序列: {{ scope.row.serialNo }}</div>
          </div>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      
      <el-table-column label="库存数量" align="center" prop="quantity" width="100px">
        <template #default="scope">
          <el-tag type="primary">{{ scope.row.quantity }}</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="锁定数量" align="center" prop="lockQuantity" width="100px">
        <template #default="scope">
          <el-tag v-if="scope.row.lockQuantity > 0" type="warning">{{ scope.row.lockQuantity }}</el-tag>
          <span v-else class="text-gray-400">0</span>
        </template>
      </el-table-column>
      
      <el-table-column label="可用数量" align="center" prop="availableQuantity" width="100px">
        <template #default="scope">
          <el-tag type="success">{{ scope.row.availableQuantity || (scope.row.quantity - scope.row.lockQuantity) }}</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="状态" align="center" width="100px">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_INVENTORY_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      
      <!-- 操作列 -->
      <el-table-column label="操作" align="center" fixed="right" width="180px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['wms:inventory:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['wms:inventory:delete']"
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
  <InventoryForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as InventoryApi from '@/api/wms/inventory'
import * as WarehouseApi from '@/api/wms/warehouse'
import * as WarehouseLocationApi from '@/api/wms/location'
import InventoryForm from './InventoryForm.vue'

defineOptions({ name: 'WmsInventory' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true) // 列表加载中
const list = ref([]) // 列表数据
const total = ref(0) // 总条数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  warehouseId: undefined,
  locationId: undefined,
  goodsName: undefined,
  skuCode: undefined,
  batchNo: undefined,
  status: undefined
})
const queryFormRef = ref()

// 仓库和库位数据
const warehouseList = ref([])
const locationList = ref([])

/**
 * 查询列表
 */
const getList = async () => {
  loading.value = true
  try {
    const data = await InventoryApi.getInventoryPage(queryParams)
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
 * 仓库变更，加载对应的库位列表
 */
const handleWarehouseChange = async (warehouseId: number) => {
  queryParams.locationId = undefined
  locationList.value = []
  
  if (warehouseId) {
    try {
      const data = await WarehouseLocationApi.getWarehouseLocationSimpleList({ warehouseId })
      locationList.value = data
    } catch (e) {
      console.error('加载库位列表失败', e)
    }
  }
}

/**
 * 打开表单
 * @param type 表单类型 'create' | 'update'
 * @param id 库存ID
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
    await message.delConfirm('只有库存数量和锁定数量均为0时才能删除！')
    await InventoryApi.deleteInventory(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/**
 * 初始化
 */
onMounted(async () => {
  // 加载仓库列表
  try {
    warehouseList.value = await WarehouseApi.getEnableWarehouseList()
  } catch (e) {
    console.error('加载仓库列表失败', e)
  }
  
  // 加载库存列表
  await getList()
})
</script>

