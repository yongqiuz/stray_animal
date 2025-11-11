<template>
  <div style="color: #666;font-size: 14px;">
    <div style="padding-bottom: 20px">
      <b>您好！{{ user.nickname }}</b>
    </div>

    <div style="margin-bottom: 60px" >
        欢迎使用本系统
        <el-divider />
    </div>

    <el-row v-loading="loading" element-loading-text="加载中...">
      <el-col :span="12">
        <div id="main" style="width: 500px; height: 400px"></div>
      </el-col>

      <el-col :span="12">
        <div id="pie" style="width: 500px; height: 400px"></div>
      </el-col>
    </el-row>

    <!-- 新增动物统计模块 -->
    <el-row style="margin-top: 40px" v-loading="loading" element-loading-text="加载中...">
      <el-col :span="12">
        <div id="animal-line" style="width: 500px; height: 400px"></div>
      </el-col>
      <el-col :span="12">
        <div id="animal-pie" style="width: 500px; height: 400px"></div>
      </el-col>
    </el-row>

    <!-- 错误提示 -->
    <el-alert
      v-if="error"
      :title="error"
      type="error"
      show-icon
      :closable="true"
      @close="error = null"
      style="margin-top: 20px"
    />
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "Home",
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      loading: false,
      error: null,
      charts: {
        main: null,
        pie: null,
        animalLine: null,
        animalPie: null
      }
    }
  },
  mounted() {  // 页面元素渲染后再触发
    this.loadData();
    // 监听窗口大小变化，重绘图表
    window.addEventListener('resize', this.resizeCharts);
  },
  beforeDestroy() {
    // 组件销毁前移除事件监听
    window.removeEventListener('resize', this.resizeCharts);
    // 销毁所有图表实例
    Object.values(this.charts).forEach(chart => {
      if (chart) {
        chart.dispose();
      }
    });
  },
  methods: {
    resizeCharts() {
      Object.values(this.charts).forEach(chart => {
        if (chart) {
          chart.resize();
        }
      });
    },
    async loadData() {
      this.loading = true;
      this.error = null;
      try {
        // 原有的用户统计图表代码
        var option = {
          title: {
            text: '各季度系统注册人数统计',
            subtext: '趋势图',
            left: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          xAxis: {
            type: 'category',
            data: ["第一季度", "第二季度", "第三季度", "第四季度"]
          },
          yAxis: {
            type: 'value',
            name: '注册人数'
          },
          series: [
            {
              data: [],
              type: 'line',
              smooth: true,
              itemStyle: { color: '#5470c6' }
            },
            {
              data: [],
              type: 'bar',
              itemStyle: { color: '#91cc75' }
            }
          ]
        };
        
        const membersRes = await this.$request.get("/echarts/members");
        option.series[0].data = membersRes.data;
        option.series[1].data = membersRes.data;
        
        if (this.charts.main) {
          this.charts.main.dispose();
        }
        this.charts.main = echarts.init(document.getElementById('main'));
        this.charts.main.setOption(option);

        // 饼图
        var pieOption = {
          title: {
            text: '各季度系统注册人数统计',
            subtext: '比例图',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            data: ['第一季度', '第二季度', '第三季度', '第四季度']
          },
          series: [
            {
              name: '注册人数',
              type: 'pie',
              radius: '60%',
              label:{
                normal:{
                  show:true,
                  position:'inner',
                  textStyle : {
                    fontWeight : 300 ,
                    fontSize : 14,
                    color: "#fff"
                  },
                  formatter:'{d}%'
                }
              },
              data: [
                {name: "第一季度", value: membersRes.data[0], itemStyle: { color: '#5470c6' }},
                {name: "第二季度", value: membersRes.data[1], itemStyle: { color: '#91cc75' }},
                {name: "第三季度", value: membersRes.data[2], itemStyle: { color: '#fac858' }},
                {name: "第四季度", value: membersRes.data[3], itemStyle: { color: '#ee6666' }}
              ],
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        };

        if (this.charts.pie) {
          this.charts.pie.dispose();
        }
        this.charts.pie = echarts.init(document.getElementById('pie'));
        this.charts.pie.setOption(pieOption);

        // 新增动物统计图表
        const animalOption = {
          title: {
            text: '流浪动物季度统计',
            subtext: '绝育与领养趋势',
            left: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          legend: {
            data: ['绝育数量', '领养数量'],
            top: 'bottom'
          },
          xAxis: {
            type: 'category',
            data: ["第一季度", "第二季度", "第三季度", "第四季度"]
          },
          yAxis: { 
            type: 'value',
            name: '数量'
          },
          series: [
            {
              name: '绝育数量',
              type: 'bar',
              data: [],
              itemStyle: { color: '#91cc75' }
            },
            {
              name: '领养数量',
              type: 'line',
              data: [],
              smooth: true,
              itemStyle: { color: '#fac858' }
            }
          ]
        };

        const adoptionPieOption = {
          title: {
            text: '领养比例分布',
            left: 'center'
          },
          tooltip: { 
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            data: ['第一季度', '第二季度', '第三季度', '第四季度']
          },
          series: [{
            name: '领养数量',
            type: 'pie',
            radius: ['40%', '70%'],
            data: [],
            label: {
              show: true,
              position: 'inside',
              formatter: '{d}%',
              fontSize: 14,
              color: '#fff'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }]
        };

        // 初始化动物统计图表
        const animalRes = await this.request.get("/echarts/animal");
        // 趋势图数据绑定
        animalOption.series[0].data = animalRes.data.sterilizations;
        animalOption.series[1].data = animalRes.data.adoptions;
        
        if (this.charts.animalLine) {
          this.charts.animalLine.dispose();
        }
        this.charts.animalLine = echarts.init(document.getElementById('animal-line'));
        this.charts.animalLine.setOption(animalOption);

        // 饼图数据转换 - 使用动物领养数据
        const pieData = animalRes.data.adoptions.map((value, index) => ({
          name: `第${index+1}季度`,
          value: value,
          itemStyle: { color: ['#5470c6','#91cc75','#fac858','#ee6666'][index] }
        }));
        adoptionPieOption.series[0].data = pieData;
        
        if (this.charts.animalPie) {
          this.charts.animalPie.dispose();
        }
        this.charts.animalPie = echarts.init(document.getElementById('animal-pie'));
        this.charts.animalPie.setOption(adoptionPieOption);
      } catch (error) {
        console.error('加载数据失败:', error);
        this.error = '加载数据失败，请稍后重试';
        this.$message.error(this.error);
      } finally {
        this.loading = false;
      }
    }
  }
}
</script>

<style scoped>
.el-alert {
  margin: 20px 0;
}
</style>
