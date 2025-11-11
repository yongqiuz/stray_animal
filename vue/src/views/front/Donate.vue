<template>
  <div class="donate-container">
    <!-- 捐赠记录表格 -->
    <el-table :data="donateRecords" border style="width: 100%">
      <el-table-column prop="time" label="日期" width="150"></el-table-column>
      <el-table-column prop="name" label="捐赠人" width="120"></el-table-column>
      <el-table-column prop="goods" label="物品"></el-table-column>
      <el-table-column prop="expressNo" label="快递单号" width="180"></el-table-column>
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button
              type="primary"
              size="mini"
              @click="queryLogistics(scope.row)"
              :disabled="!scope.row.expressNo"
          >
            物流追踪
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 物流详情对话框 -->
    <el-dialog
        title="物流详情"
        :visible.sync="dialogVisible"
        width="60%"
    >
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading"><loading /></el-icon>
        正在查询物流信息...
      </div>
      <div v-else-if="logisticsDetail">
        <div class="logistics-header">
          <div class="logistics-info">
            <span>快递公司：{{ getExpressCompanyName(logisticsDetail.com) }}</span>
            <span class="ml-20">快递单号：{{ logisticsDetail.nu }}</span>
          </div>
          <div v-if="logisticsDetail.state" class="logistics-state">
            <el-tag :type="getStateType(logisticsDetail.state)">
              {{ getStateText(logisticsDetail.state) }}
            </el-tag>
          </div>
        </div>

        <el-timeline v-if="logisticsDetail.data && logisticsDetail.data.length">
          <el-timeline-item
              v-for="(item, index) in logisticsDetail.data"
              :key="index"
              :timestamp="item.ftime || item.time"
              :type="index === 0 ? 'primary' : ''"
          >
            <div class="timeline-content">
              <p class="context">{{ item.context }}</p>
              <p v-if="item.location" class="location">
                <i class="el-icon-location"></i> {{ item.location }}
              </p>
            </div>
          </el-timeline-item>
        </el-timeline>
        <div v-else class="no-data">
          暂无物流信息
        </div>
      </div>
      <div v-else class="error-container">
        {{ errorMsg || '物流信息获取失败' }}
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dialogVisible: false,
      logisticsDetail: null,
      loading: false,
      errorMsg: '',
      donateRecords: [],
      expressCompanies: {
        'yunda': '圆通速递',
        'zhongtong': '中通快递',
        'shunfeng': '顺丰速运',
        'yuantong': '圆通速递',
        'ems': 'EMS',
        'shentong': '申通快递',
        'huitongkuaidi': '汇通快运',
        'jd': '京东物流',
        'zhaijisong': '宅急送',
        'tiantian': '天天快递'
      },
      expressStates: {
        '0': { text: '在途', type: 'info' },
        '1': { text: '揽收', type: 'primary' },
        '2': { text: '疑难', type: 'warning' },
        '3': { text: '签收', type: 'success' },
        '4': { text: '退签', type: 'danger' },
        '5': { text: '派件', type: 'primary' },
        '6': { text: '退回', type: 'danger' },
        '7': { text: '转投', type: 'warning' },
        '8': { text: '清关', type: 'info' },
        '14': { text: '拒签', type: 'danger' }
      }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/donate/page", {
        params: {
          pageNum: 1,
          pageSize: 10
        }
      }).then(res => {
        if (res.code === '200') {
          this.donateRecords = res.data.records
        }
      })
    },
    // 查询物流信息
    async queryLogistics(row) {
      if (!row.expressNo) {
        this.$message.warning('该记录暂无快递单号')
        return
      }

      try {
        this.dialogVisible = true
        this.loading = true
        this.logisticsDetail = null
        this.errorMsg = ''

        const response = await this.request.get(`/donate/express/yuantong/${row.expressNo}`)
        console.log('物流查询响应:', response)
        
        if (response.code === '200' && response.data) {
          this.logisticsDetail = response.data
        } else {
          this.errorMsg = response.msg || '物流信息查询失败'
          console.error('物流查询错误:', response)
        }
      } catch (error) {
        console.error('请求错误:', error)
        this.errorMsg = error.response?.data?.msg || error.message || '请求失败'
      } finally {
        this.loading = false
      }
    },
    // 获取快递公司名称
    getExpressCompanyName(code) {
      return this.expressCompanies[code] || code
    },
    // 获取物流状态文本
    getStateText(state) {
      return this.expressStates[state]?.text || '未知状态'
    },
    // 获取物流状态类型
    getStateType(state) {
      return this.expressStates[state]?.type || 'info'
    }
  }
}
</script>

<style scoped>
.donate-container {
  padding: 20px;
}

.logistics-header {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logistics-info {
  font-size: 16px;
}

.ml-20 {
  margin-left: 20px;
}

.loading-container {
  text-align: center;
  padding: 40px 0;
}

.error-container {
  text-align: center;
  padding: 40px 0;
  color: #f56c6c;
}

.no-data {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

.el-timeline {
  max-height: 400px;
  overflow-y: auto;
  padding: 20px;
}

.timeline-content {
  padding: 5px 0;
}

.timeline-content .context {
  margin: 0;
  font-size: 14px;
}

.timeline-content .location {
  margin: 5px 0 0;
  font-size: 12px;
  color: #909399;
}

.el-timeline-item:first-child {
  color: #409EFF;
}
</style>


