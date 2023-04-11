import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ImgashAddComponent } from './components/imgash-add/imgash-add.component';
import { ImgashListComponent } from './components/imgash-list/imgash-list.component';
import { ScanjobListComponent } from './components/scanjob-list/scanjob-list.component';
import { SearchinindexComponent } from './components/searchinindex/searchinindex.component';

const routes: Routes = [
  { path: 'job-list', component: ScanjobListComponent },
  { path: 'imghash-list', component: ImgashListComponent },
  { path: 'metadata-index', component: SearchinindexComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

