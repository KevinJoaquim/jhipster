import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IResourceGot } from '../resource-got.model';

@Component({
  selector: 'jhi-resource-got-detail',
  templateUrl: './resource-got-detail.component.html',
})
export class ResourceGotDetailComponent implements OnInit {
  resourceGot: IResourceGot | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resourceGot }) => {
      this.resourceGot = resourceGot;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
