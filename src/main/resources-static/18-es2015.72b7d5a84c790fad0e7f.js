(window.webpackJsonp=window.webpackJsonp||[]).push([[18],{"82nU":function(t,n,o){"use strict";o.r(n),o.d(n,"MainPageModule",(function(){return g}));var i=o("ofXK"),e=o("3Pt+"),a=o("TEn/"),r=o("tyNb"),s=o("fXoL"),h=o("oNol"),c=o("v5k/");const b=["splashDiv"];let u=(()=>{class t{constructor(t,n){this.platform=t,this.auth=n,this.auth.setStorage(c.a.NAVIGATION_EXTRA,"home"),this.initializeApp()}initializeApp(){this.auth.platform.ready().then(()=>{this.auth.getUser().then(t=>{this.auth.navigateRoot(t?"/home":"/login")},t=>{this.auth.removeStorage(c.a.USER),this.auth.user=null,this.auth.navigateRoot("/login")})})}login(){this.auth.navigateRoot("/login")}logout(){this.auth.logout(),this.auth.navigateRoot("/login")}ionViewWillEnter(){this.auth.setTitle("\ud648"),this.auth.menuClose()}apiLink(){window.document.location.href="https://www.korbit.co.kr"}}return t.\u0275fac=function(n){return new(n||t)(s.Jb(a.A),s.Jb(h.a))},t.\u0275cmp=s.Db({type:t,selectors:[["page-main"]],viewQuery:function(t,n){var o;1&t&&s.kc(b,!0,s.l),2&t&&s.dc(o=s.Ub())&&(n.splashDiv=o.first)},decls:18,vars:1,consts:[["color","--bory-blue"],["slot","start"],["color","light"],[1,"ion-padding"],[1,"titleArea"],[1,"userProfile"],["src","assets/svg/korbit.svg"],["src","assets/svg/push.svg"]],template:function(t,n){1&t&&(s.Mb(0,"ion-header"),s.Mb(1,"ion-toolbar",0),s.Mb(2,"ion-buttons",1),s.Kb(3,"ion-menu-button",2),s.Lb(),s.Mb(4,"ion-title"),s.hc(5),s.Lb(),s.Lb(),s.Lb(),s.Mb(6,"ion-content",3),s.Mb(7,"div",4),s.Mb(8,"h3"),s.hc(9,"Fisherman"),s.Lb(),s.Mb(10,"h3"),s.hc(11," Fisherman \uc740 Korbit \uc804\uc6a9 \ud478\uc26c \uc11c\ube44\uc2a4 \uc11c\ubc84 \uc785\ub2c8\ub2e4. "),s.Lb(),s.Lb(),s.Mb(12,"div",5),s.Kb(13,"ion-img",6),s.Kb(14,"p"),s.Lb(),s.Mb(15,"div",5),s.Kb(16,"p"),s.Kb(17,"ion-img",7),s.Lb(),s.Lb()),2&t&&(s.zb(5),s.jc(" Fisherman::",n.auth.title," "))},directives:[a.h,a.t,a.d,a.o,a.s,a.f,a.i],styles:["ion-content[_ngcontent-%COMP%]{position:relative!important;width:100%;height:auto;margin-bottom:0}ion-content[_ngcontent-%COMP%]   .titleArea[_ngcontent-%COMP%]{height:20%;width:100%;padding:1em;color:var(--bory-blue);font-size:1em}ion-content[_ngcontent-%COMP%]   .userProfile[_ngcontent-%COMP%]{height:auto;width:100%;padding:1em;text-align:center}ion-content[_ngcontent-%COMP%]   .userProfile[_ngcontent-%COMP%]   ion-img[_ngcontent-%COMP%]{margin:0 auto;width:30vw;min-width:256px;height:auto;border-radius:50%}ion-fab[_ngcontent-%COMP%]{width:50px;height:50px}ion-fab[_ngcontent-%COMP%]   ion-fab-button[_ngcontent-%COMP%]{margin-bottom:7em;background-color:var(--bory-blue)}ion-fab[_ngcontent-%COMP%]   ion-fab-button[_ngcontent-%COMP%]   .fab[_ngcontent-%COMP%]   ion-icon[_ngcontent-%COMP%]{color:#fff}"]}),t})(),g=(()=>{class t{}return t.\u0275mod=s.Hb({type:t}),t.\u0275inj=s.Gb({factory:function(n){return new(n||t)},imports:[[i.b,e.d,e.h,a.u,r.i.forChild([{path:"",component:u}])]]}),t})()}}]);