(window.webpackJsonp=window.webpackJsonp||[]).push([[17],{F4UR:function(n,e,t){"use strict";t.r(e),t.d(e,"LoginPageModule",(function(){return d}));var o=t("ofXK"),i=t("3Pt+"),r=t("TEn/"),a=t("tyNb"),g=t("oNol"),l=t("v5k/"),s=t("fXoL");let c=(()=>{class n{constructor(n,e){this.auth=n,this.formBuilder=e,this.isFacebookLogin=!1,this.registerCredentials={email:"",password:""},this.auth.logout(),this.loginForm=this.formBuilder.group({email:["",[i.j.required,i.j.pattern("^[a-zA-Z0-9.!#$%&\u2019*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:.[a-zA-Z0-9-]+)*$")]],password:["",[i.j.required,i.j.minLength(7)]]})}signup(){this.auth.navigateForward("/signup/select")}login(){if(!this.loginForm.valid)for(const n in this.loginForm.controls)if(!this.loginForm.controls[n].valid)return this.auth.showError(n+" : "+this.auth.message.get("form.field.invalid")),!1;this.auth.login(this.registerCredentials).then(n=>{n&&200===n.code?(this.registerCredentials.password=n.message,this.auth.user=this.registerCredentials,this.auth.setStorage(l.a.USER,this.auth.user).then(n=>{this.auth.initMenu(),this.auth.navigateRoot("/home")},n=>{this.auth.presentAlert(this.auth.message.get("login.session.invalid"))})):this.auth.presentAlert(n.message)},n=>{this.auth.removeStorage(l.a.USER),this.auth.user=null,this.auth.presentAlert(this.auth.message.get("login.fails"))})}}return n.\u0275fac=function(e){return new(e||n)(s.Jb(g.a),s.Jb(i.a))},n.\u0275cmp=s.Db({type:n,selectors:[["page-login"]],decls:24,vars:10,consts:[[1,"ion-padding"],[1,"container"],[3,"formGroup"],[1,"introTitle"],[1,"defaultTitle"],[1,"defaultInput"],["size","9",1,"ion-no-padding"],["type","text","formControlName","email","required","",1,"ion-no-padding",3,"ngModel","placeholder","ngModelChange"],["required","","type","password","formControlName","password","type","password","required","",1,"ion-no-padding",3,"ngModel","placeholder","ngModelChange"],[1,"autoLogin"],[1,"login",3,"click"],["color","white",1,"signup",3,"click"]],template:function(n,e){1&n&&(s.Mb(0,"ion-content",0),s.Mb(1,"div",1),s.Mb(2,"form",2),s.Mb(3,"ion-row",3),s.Mb(4,"label",3),s.hc(5),s.Lb(),s.Lb(),s.Mb(6,"ion-row",4),s.Mb(7,"label",4),s.hc(8),s.Lb(),s.Lb(),s.Mb(9,"ion-row",5),s.Mb(10,"ion-col",6),s.Mb(11,"ion-input",7),s.Tb("ngModelChange",(function(n){return e.registerCredentials.email=n})),s.Lb(),s.Lb(),s.Lb(),s.Mb(12,"ion-row",4),s.Mb(13,"label",4),s.hc(14),s.Lb(),s.Lb(),s.Mb(15,"ion-row",5),s.Mb(16,"ion-col",6),s.Mb(17,"ion-input",8),s.Tb("ngModelChange",(function(n){return e.registerCredentials.password=n})),s.Lb(),s.Lb(),s.Lb(),s.Lb(),s.Mb(18,"ion-row",9),s.Mb(19,"ion-button",10),s.Tb("click",(function(){return e.login()})),s.hc(20),s.Lb(),s.Lb(),s.Mb(21,"ion-row"),s.Mb(22,"ion-button",11),s.Tb("click",(function(){return e.signup()})),s.hc(23),s.Lb(),s.Lb(),s.Lb(),s.Lb()),2&n&&(s.zb(2),s.Yb("formGroup",e.loginForm),s.zb(3),s.ic(e.auth.message.get("login")),s.zb(3),s.ic(e.auth.message.get("email")),s.zb(3),s.Zb("placeholder",e.auth.message.get("email")),s.Yb("ngModel",e.registerCredentials.email),s.zb(3),s.ic(e.auth.message.get("password")),s.zb(3),s.Zb("placeholder",e.auth.message.get("password")),s.Yb("ngModel",e.registerCredentials.password),s.zb(3),s.ic(e.auth.message.get("login")),s.zb(3),s.ic(e.auth.message.get("signup")))},directives:[r.f,i.k,i.g,i.c,r.r,r.e,r.j,r.C,i.f,i.b,i.i,r.c],styles:["ion-content[_ngcontent-%COMP%]{padding:30px}ion-content[_ngcontent-%COMP%]   form[_ngcontent-%COMP%]   ion-row[_ngcontent-%COMP%]   ion-col[_ngcontent-%COMP%]   ion-input[_ngcontent-%COMP%]{font-size:1.5rem}","div.container[_ngcontent-%COMP%] {\n    flex-direction: column;\n    padding: 0rem 1.5rem;\n  }\n\n  \n  label.introTitle[_ngcontent-%COMP%] {\n    font-size: 2.8rem;\n    margin-top: 6rem;\n    margin-bottom: 1rem;\n    margin-left: 0.3rem;\n  }\n\n  \n  label.defaultTitle[_ngcontent-%COMP%] {\n    font-size: 1.2rem;\n    font-weight: bold;\n    margin-bottom: 1rem;\n  }\n\n  ion-row.defaultTitle[_ngcontent-%COMP%] {\n    margin-top: 4rem;\n  }\n\n  \n  ion-input.defaultInput[_ngcontent-%COMP%] {\n    text-align: left;\n    font-size: 1.5rem;\n    border: solid 0.1rem white;\n    border-bottom-color: var(--bory-border);\n    --placeholder-color: var(--bory-placeholder);\n    --padding-top: 0.3rem;\n    --padding-bottom: 0.3rem;\n    width: 22rem;\n    background: inherit;\n  }\n\n\n  ion-row.autoLogin[_ngcontent-%COMP%] {\n    margin-top: 2em;\n  }\n\n  ion-label.autoLogin[_ngcontent-%COMP%] {\n    font-size: 1.2rem;\n    display: flex;\n    align-items: center;\n    color: var(--bory-grey);\n    margin-left: 0.5rem;\n  }\n\n\n  \n  ion-button.login[_ngcontent-%COMP%] {\n    width: 100%;\n    height: 3.6rem;\n    font-size: 1.2rem;\n    text-align: center;\n    display: block;\n    background-color: var(--bory-blue);\n    color: white;\n    border-radius: 1.8rem;\n    margin-left: auto;\n    margin-top: 1rem;\n  }\n\n\n  \n  ion-row.search[_ngcontent-%COMP%] {\n    margin-top: 1rem;\n  }\n\n  button.search[_ngcontent-%COMP%] {\n    color: var(--bory-placeholder);\n    font-size: 1.1rem;\n    background-color: white;\n  }\n\n\n  \n  ion-button.signup[_ngcontent-%COMP%] {\n    width: 100%;\n    height: 5rem;\n    font-size: 1.2rem;\n    font-weight: 400;\n    text-align: center;\n    color: var(--bory-blue);\n    background: white;\n    border: 0.2rem solid var(--bory-blue);\n    margin-top: 2rem;\n    margin-bottom: 1.5rem;\n  }\n\n  ion-row.snsLogin[_ngcontent-%COMP%] {\n    width: 100%;\n  }\n\n  ion-col.snsLogin[_ngcontent-%COMP%] {\n    display: flex;\n    align-items: center;\n  }\n\n  button.facebookLogin[_ngcontent-%COMP%] {\n    width: 100%;\n    height: 5rem;\n    font-size: 1.2rem;\n    text-align: center;\n    color: white;\n    background: var(--bory-facebook) !important;\n    display: flex;\n    align-items: center;\n    padding-right: 4rem;\n  }\n\n  button.googleLogin[_ngcontent-%COMP%] {\n    width: 100%;\n    height: 5rem;\n    font-size: 1.2rem;\n    text-align: center;\n    color: white;\n    background: var(--bory-google) !important;\n    display: flex;\n    align-items: center;\n    padding-right: 4rem;\n    margin-top: 0.5rem;\n  }\n\n  label.snsLogin[_ngcontent-%COMP%] {\n    width: 100%;\n    text-align: center;\n    padding-right: 1.2rem;\n  }\n\n  ion-img.facebookLogo[_ngcontent-%COMP%] {\n    width: 4rem;\n    height: 4rem;\n    text-align: left;\n    float: left;\n  }\n\n  ion-img.googleLogo[_ngcontent-%COMP%] {\n    width: 4rem;\n    height: 4rem;\n    text-align: left;\n    float: left;\n  }"]}),n})(),d=(()=>{class n{}return n.\u0275mod=s.Hb({type:n}),n.\u0275inj=s.Gb({factory:function(e){return new(e||n)},imports:[[o.b,i.d,i.h,r.u.forRoot(),a.i.forChild([{path:"",component:c}])]]}),n})()}}]);