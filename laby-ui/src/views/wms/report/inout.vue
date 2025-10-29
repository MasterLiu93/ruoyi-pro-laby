<!--
  出入库统计页面
  
  功能说明：
  1. 出入库汇总统计
  2. 出入库趋势图（折线图）
  3. 支持按时间范围、仓库等筛选
  
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
            <div class="statistic-title">总入库数量</div>
            <div class="statistic-value text-success">{{ summary.totalInboundQuantity || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">入库单数</div>
            <div class="statistic-value">{{ summary.totalInboundOrderCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">总出库数量</div>
            <div class="statistic-value text-warning">{{ summary.totalOutboundQuantity || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">出库单数</div>
            <div class="statistic-value">{{ summary.totalOutboundOrderCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">净库存变化</div>
            <div class="statistic-value" :class="summary.netChange >= 0 ? 'text-primary' : 'text-danger'">
              {{ summary.netChange || 0 }}
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover">
          <div class="statistic-card">
            <div class="statistic-title">库存周转率</div>
            <div class="statistic-value text-info">{{ summary.turnoverRate || 0 }}%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true">
      <el-form-item label="时间范围" prop="startTime">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD HH:mm:ss"
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

      <el-form-item>
        <el-button @click="handleQuery">搜索</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 图表 -->
  <ContentWrap>
    <el-card shadow="never">
      <div ref="chartRef" style="height: 400px;"></div>
    </el-card>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="日期" prop="statisticDate" width="120" align="center" :formatter="dateFormatter" />
      <el-table-column label="入库数量" prop="inboundQuantity" width="120" align="center">
        <template #default="scope">
          <span class="text-success">{{ scope.row.inboundQuantity }}</span>
        </template>
      </el-table-column>
      <el-table-column label="入库单数" prop="inboundOrderCount" width="100" align="center" />
      <el-table-column label="出库数量" prop="outboundQuantity" width="120" align="center">
        <template #default="scope">
          <span class="text-warning">{{ scope.row.outboundQuantity }}</span>
        </template>
      </el-table-column>
      <el-table-column label="出库单数" prop="outboundOrderCount" width="100" align="center" />
      <el-table-column label="净变化" prop="netChange" width="120" align="center">
        <template #default="scope">
          <span :class="scope.row.netChange >= 0 ? 'text-primary' : 'text-danger'">
            {{ scope.row.netChange }}
          </span>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'
import { formatDate } from '@/utils/formatTime'
import * as InOutReportApi from '@/api/wms/inOutReport'
import * as WarehouseApi from '@/api/wms/warehouse'

defineOptions({ name: 'WmsInOutReport' })

const loading = ref(true)
const list = ref([])
const warehouseList = ref([])
const chartRef = ref()
const dateRange = ref([])
const summary = ref({
  totalInboundQuantity: 0,
  totalInboundOrderCount: 0,
  totalOutboundQuantity: 0,
  totalOutboundOrderCount: 0,
  netChange: 0,
  turnoverRate: 0
})

const queryParams = reactive({
  startTime: undefined,
  endTime: undefined,
  warehouseId: undefined
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await InOutReportApi.getInOutReportList(queryParams)
    list.value = data
    renderChart()
  } finally {
    loading.value = false
  }
}

const getSummary = async () => {
  const data = await InOutReportApi.getInOutReportSummary(queryParams)
  summary.value = data
}

const renderChart = () => {
  if (!chartRef.value) return

  const chart = echarts.init(chartRef.value)
  const dates = list.value.map(item => formatDate(item.statisticDate, 'MM-DD'))
  const inboundData = list.value.map(item => item.inboundQuantity)
  const outboundData = list.value.map(item => item.outboundQuantity)
  const netChangeData = list.value.map(item => item.netChange)

  const option = {
    title: {
      text: '出入库趋势图',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['入库数量', '出库数量', '净变化'],
      top: 30
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '入库数量',
        type: 'line',
        data: inboundData,
        itemStyle: { color: '#67c23a' },
        areaStyle: { opacity: 0.3 }
      },
      {
        name: '出库数量',
        type: 'line',
        data: outboundData,
        itemStyle: { color: '#e6a23c' },
        areaStyle: { opacity: 0.3 }
      },
      {
        name: '净变化',
        type: 'line',
        data: netChangeData,
        itemStyle: { color: '#409eff' }
      }
    ]
  }

  chart.setOption(option)
}

const handleQuery = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.startTime = dateRange.value[0]
    queryParams.endTime = dateRange.value[1]
  } else {
    queryParams.startTime = undefined
    queryParams.endTime = undefined
  }
  getList()
  getSummary()
}

const resetQuery = () => {
  dateRange.value = []
  queryFormRef.value.resetFields()
  queryParams.startTime = undefined
  queryParams.endTime = undefined
  handleQuery()
}

const dateFormatter = (row, column, cellValue) => {
  if (!cellValue) return '-'
  return formatDate(cellValue, 'YYYY-MM-DD')
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

