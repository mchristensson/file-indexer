import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClient, HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ScanjobAddComponent } from './components/scanjob-add/scanjob-add.component';
import { ScanjobListComponent } from './components/scanjob-list/scanjob-list.component';
import { SearchinindexComponent } from './components/searchinindex/searchinindex.component';

@NgModule({
  declarations: [
    AppComponent,
    ScanjobAddComponent,
    ScanjobListComponent,
    SearchinindexComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
