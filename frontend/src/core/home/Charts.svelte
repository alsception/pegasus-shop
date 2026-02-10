
<script lang="ts">
  import { onMount } from "svelte";
  import { getCurrentRole } from "../services/SessionStore";



  onMount(() => {
    processGoogleChart();
  });

  function processGoogleChart()
  {
      google.charts.load('current', {'packages':['bar']});
      google.charts.setOnLoadCallback(drawChart);

      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          ['Dan', 'Promet (€)'],
          ['30 Jan', 1250],
          ['31 Jan', 1580],
          ['01 Feb', 920],
          ['02 Feb', 1740],
          ['03 Feb', 1320],
          ['04 Feb', 1890],
          ['05 Feb', 2100],
          ['06 Feb', 1650],
          ['07 Feb', 1980],
          ['Danas', 850]
        ]);

        var options = {
          chart: {
            title: 'Promet po danima',
            subtitle: 'Zadnjih 10 dana',
          },
          colors: ['#1b9e77'],
          height: 400,
              backgroundColor_: 'transparent',
              backgroundColor: {
              fill: '#F4F4F4',
              opacity: 100
          },

        };

        var chart = new google.charts.Bar(document.getElementById('chart_div'));
        chart.draw(data, google.charts.Bar.convertOptions(options));
      }

  }

</script><!-- 

<svelte:head>
  <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
</svelte:head>
 -->

<div class="stats-wrapper">
  <div class="stats-container">
    <!-- Google Chart -->
    <div class="chart-card">
      <div id="chart_div"></div>
    </div>

    <!-- Pie Chart -->
    <div class="chart-card">
      <h2>Statistika narudžbi</h2>
      <div class="chart-container">
        <div class="pie-chart"></div>
        
        <ul class="legend">
          <li><span class="dot" style="background:#4CAF50"></span> Restoran (30%)</li>
          <li><span class="dot" style="background:#2196F3"></span> Dostava (25%)</li>
          <li><span class="dot" style="background:#FFC107"></span> Wolt (25%)</li>
          <li><span class="dot" style="background:#FF5722"></span> Takeaway (20%)</li>
        </ul>
      </div>
    </div>
  </div>
</div>




<style>
.chart-container {
    display: flex;
    align-items: center;
    font-family: sans-serif;
    gap: 20px;
  }

  .pie-chart {
    width: 200px;
    height: 200px;
    border-radius: 50%;
    /* Postotke podešavaš ovdje: */
    background: conic-gradient(
      #4caf4f7c 0% 80%,      /* Restoran - 30% */
      #2195f39c 30% 55%,     /* Dostava - 25% */
      #FFC107 55% 80%,     /* Wolt - 25% */
      #FF5722 80% 100%     /* Takeaway - 20% */
    );
  }

  .legend {
    list-style: none;
    padding: 0;
  }

  .legend li {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }

  .dot {
    width: 12px;
    height: 12px;
    border-radius: 2px;
    margin-right: 8px;
  }

  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
    padding: 2rem 1rem;
/*     max-width: 1400px;
 */    margin: 0 auto;
    margin-top: -3rem;
  }


  @keyframes slideIn {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }


  @media (max-width: 640px) {
    .stats-container {
      grid-template-columns: 1fr;
      padding: 1rem;
    }

  }

   .stats-wrapper {
    width: 100%;
    padding: 20px;
  }

  .stats-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 20px;
/*     max-width: 1400px;
 */    margin: 0 auto;
  }

  .chart-card {
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }

  .chart-card h2 {
    margin-top: 0;
    margin-bottom: 20px;
    font-size: 1.5rem;
    color: #333;
  }

  .chart-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }

  .pie-chart {
    width: 200px;
    height: 200px;
    border-radius: 50%;
    background: conic-gradient(
      #4CAF50 0% 30%,
      #2196F3 30% 55%,
      #FFC107 55% 80%,
      #FF5722 80% 100%
    );
  }

  .legend {
    list-style: none;
    padding: 0;
    margin: 0;
    width: 100%;
  }

  .legend li {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 0;
    font-size: 0.95rem;
  }

  .dot {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    display: inline-block;
  }

  /* Mobile optimization */
  @media (max-width: 768px) {
    .stats-wrapper {
      padding: 10px;
    }

    .stats-container {
      grid-template-columns: 1fr;
      gap: 15px;
    }

    .chart-card {
      padding: 15px;
    }

    .chart-card h2 {
      font-size: 1.25rem;
    }

    .pie-chart {
      width: 180px;
      height: 180px;
    }
  }

  @media (max-width: 480px) {
    .pie-chart {
      width: 150px;
      height: 150px;
    }

    .legend li {
      font-size: 0.9rem;
    }
  }
</style>