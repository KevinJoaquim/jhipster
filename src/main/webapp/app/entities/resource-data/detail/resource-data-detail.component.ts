import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IResourceData } from '../resource-data.model';

@Component({
  selector: 'jhi-resource-data-detail',
  templateUrl: './resource-data-detail.component.html',
})
export class ResourceDataDetailComponent implements OnInit {
  resourceData: IResourceData | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resourceData }) => {
      this.resourceData = resourceData;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
