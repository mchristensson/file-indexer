import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ScanjobAddComponent } from './components/scanjob-add/scanjob-add.component';
import { ScanjobListComponent } from './components/scanjob-list/scanjob-list.component';
import { SearchinindexComponent } from './components/searchinindex/searchinindex.component';
import { ImgashListComponent } from './components/imgash-list/imgash-list.component';
import { ImgashAddComponent } from './components/imgash-add/imgash-add.component';
import { DeviceAddComponent } from './components/device-add/device-add.component';
import { ModalformComponent } from './components/modalform/modalform.component';

@NgModule({
  declarations: [
    AppComponent,
    ScanjobAddComponent,
    ScanjobListComponent,
    SearchinindexComponent,
    ImgashListComponent,
    ImgashAddComponent,
    DeviceAddComponent,
    ModalformComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
