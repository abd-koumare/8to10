<template>
  <ion-page>
    <ion-content class="Content-Home" :fullscreen="true">
      <div class="Home">
        <div class="Home-logo">
          <img src="/assets/icon/Logo-8to10.svg" alt="" />
        </div>

        <div class="Home-title">
          <span>Passez de huit à dix chiffres.</span>
        </div>

        <div class="Home-localization">
          <span> Uniquement pour la côte d'ivoire ! </span>
        </div>

        <div class="Home-subtitle">
          <span>
            Vos numéros de téléphone locaux passeront de 8 à 10 chiffres, afin
            de satisfaire les besoins en numéro mobile de certains opérateurs
            qui sont arrivés à saturation.
          </span>
        </div>

        <div class="Button">
          <!-- @click="AppConvertions" -->
          <div class="StartButtonClass">
            <div class="Button-Actived">
              <a
                @click.prevent="FormConvert"
                style="background-color: #C1D9FF;"
                class="Button-area-shadow Button-area-shadow-Home"
              >
                <div
                  style="background-color:#3880ff;  "
                  class="Button-area-ligth Button-area-ligth-Home"
                >
                  <img src="/assets/icon/Icon-arrow-right.svg" alt="" />
                </div>
              </a>
            </div>
          </div>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script  >
import { IonPage, IonContent } from "@ionic/vue";

export default {
  name: "Home",
  components: { IonContent, IonPage },
  setup() {
    const FormConvert = () => {
      window.location.href = "/tabs/tab1/";
    };

    return {
      FormConvert,
    };
  },

  methods: {},
};
</script>


<style scoped >

@font-face {
	font-family: 'Maven Pro';
	src: url('/assets/MavenPro-Medium.ttf'), url('/assets/MavenPro-Regular.ttf');
}
*{
    font-family: 'Maven Pro' !important;
}
.Home {
  width: 100%;
  height: 100%;
  background-color: rgba(#71c9ce, 0.03) !important;

  display: flex;
  justify-content: center;
  flex-direction: column;
  padding-left: 32px;
  padding-right: 32px;

  
}

.Home-logo {
    display: flex;
    justify-content: start;
    margin-bottom: 8px;
  }
  .Home-logo img {
      height: 32px;
    }

  .Home-title {
    font-family: "Maven Pro" !important;
    font-size: 3.2em;

    position: relative;
    line-height: 1.2em;
    color: #3880ff !important;
  }

  .Home-localization {
    font-family: "Jost";
    font-weight: 600;
    font-size: 1.1em;
    margin: 8px 0px 16px 0px;
    color: #d84685 !important;
  }

  .Home-subtitle {
    font-family: "Jost";
    font-weight: 400;
    font-size: 1em;
    position: relative;
    line-height: 1.5em;
    color: rgba(#182b3a, 0.7) !important;
  }

.Button {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 32px auto 0px auto;
}

.Button-area-shadow-Home {
  width: 4.5em;
  height: 4.5em;
}
.Button-area-ligth-Home {
  width: 3em;
  height: 3em;

  
}

.Button-area-ligth-Home img {
    height: 16px !important;
  }

.Button-area-shadow {
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 100%;

  
}


.Button-area-shadow .Button-area-ligth {
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 100%;
    box-shadow: 0px 0px 20px rgba(#e5e5e5, 0.66);

    
  }

 .Button-area-shadow .Button-area-ligth img {
      height: 48px;
    }

@media screen and (min-height: 730px) and (max-height: 2000px) {
  .Home-title {
    font-size: 4em !important;
  }

  .Home-localization {
    font-size: 1.2em;
  }

  .Home-subtitle {
    font-size: 1.1em !important;
    color: #182b3a !important;
    opacity: 0.8;
  }
}
</style>