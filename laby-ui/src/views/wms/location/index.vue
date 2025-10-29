<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="仓库" prop="warehouseId">
        <el-select
          v-model="queryParams.warehouseId"
          placeholder="请选择仓库"
          clearable
          @change="handleWarehouseChange"
          class="!w-240px"
        >
          <el-option
            v-for="warehouse in warehouseList"
            :key="warehouse.id"
            :label="warehouse.warehouseName"
            :value="warehouse.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="库区" prop="areaId">
        <el-select
          v-model="queryParams.areaId"
          placeholder="请选择库区"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="area in areaList"
            :key="area.id"
            :label="area.areaName"
            :value="area.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="库位编码" prop="locationCode">
        <el-input
          v-model="queryParams.locationCode"
          placeholder="请输入库位编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="库位类型" prop="locationType">
        <el-select
          v-model="queryParams.locationType"
          placeholder="请选择库位类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_WAREHOUSE_LOCATION_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.WMS_LOCATION_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['wms:location:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['wms:location:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="库位编码" align="center" prop="locationCode" width="160px" />
      <el-table-column label="仓库" align="center" prop="warehouseName" />
      <el-table-column label="库区" align="center" prop="areaName" />
      <el-table-column label="排号" align="center" prop="rowNo" />
      <el-table-column label="列号" align="center" prop="columnNo" />
      <el-table-column label="层号" align="center" prop="layerNo" />
      <el-table-column label="库位类型" align="center" prop="locationType" width="120px">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_WAREHOUSE_LOCATION_TYPE" :value="scope.row.locationType" />
        </template>
      </el-table-column>
      <el-table-column label="容量(㎡)" align="center" prop="capacity" width="100px" />
      <el-table-column label="最大承重(kg)" align="center" prop="maxWeight" width="120px" />
      <el-table-column label="状态" align="center" prop="status" width="100px">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.WMS_LOCATION_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" width="200px" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['wms:location:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['wms:location:delete']"
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

  <!-- 表单弹窗：添加/修改 -->
  <WarehouseLocationForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import * as WarehouseLocationApi from '@/api/wms/location'
import * as WarehouseApi from '@/api/wms/warehouse'
import * as WarehouseAreaApi from '@/api/wms/area'
import WarehouseLocationForm from './WarehouseLocationForm.vue'

/** 库位管理 列表 */
defineOptions({ name: 'WmsWarehouseLocation' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  warehouseId: undefined,
  areaId: undefined,
  locationCode: undefined,
  locationType: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const warehouseList = ref([]) // 仓库列表
const areaList = ref([]) // 库区列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WarehouseLocationApi.getWarehouseLocationPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 仓库切换 */
const handleWarehouseChange = async (warehouseId: number) => {
  queryParams.areaId = undefined
  if (warehouseId) {
    // 加载库区列表
    areaList.value = await WarehouseAreaApi.getWarehouseAreaListByWarehouseId(warehouseId)
  } else {
    areaList.value = []
  }
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await WarehouseLocationApi.deleteWarehouseLocation(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await WarehouseLocationApi.exportWarehouseLocation(queryParams)
    download.excel(data, '库位管理.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 加载仓库列表
  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
  // 如果有选中的仓库，加载对应的库区列表
  if (queryParams.warehouseId) {
    areaList.value = await WarehouseAreaApi.getWarehouseAreaListByWarehouseId(queryParams.warehouseId)
  }
})
</script>

