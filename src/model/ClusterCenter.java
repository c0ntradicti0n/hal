package model;


import java.io.Serializable;

import com.opencsv.bean.CsvBindByName;

import jMonkeyGod.Point3D;


public class ClusterCenter implements Serializable
{
	   private static final long serialVersionUID = 1L;
	   private  int cl;

	public int getCl() {
		return cl;
	}

	public void setCl(int cl) {
		this.cl = cl;
	}

	public float getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getX_pca() {
		return x_pca;
	}

	public void setX_pca(float x_pca) {
		this.x_pca = x_pca;
	}

	public float getY_pca() {
		return y_pca;
	}

	public void setY_pca(float y_pca) {
		this.y_pca = y_pca;
	}

	public float getZ_pca() {
		return z_pca;
	}

	public void setZ_pca(float z_pca) {
		this.z_pca = z_pca;
	}

	public float getX_tsne() {
		return x_tsne;
	}

	public void setX_tsne(float x_tsne) {
		this.x_tsne = x_tsne;
	}

	public float getY_tsne() {
		return y_tsne;
	}

	public void setY_tsne(float y_tsne) {
		this.y_tsne = y_tsne;
	}

	public float getZ_tsne() {
		return z_tsne;
	}

	public void setZ_tsne(float z_tsne) {
		this.z_tsne = z_tsne;
	}

	public float getX_k2() {
		return x_k2;
	}

	public void setX_k2(float x_k2) {
		this.x_k2 = x_k2;
	}

	public float getY_k2() {
		return y_k2;
	}

	public void setY_k2(float y_k2) {
		this.y_k2 = y_k2;
	}

	public float getZ_k2() {
		return z_k2;
	}

	public void setZ_k2(float z_k2) {
		this.z_k2 = z_k2;
	}

	public int getCl_pca() {
		return cl_pca;
	}

	public void setCl_pca(int cl_pca) {
		this.cl_pca = cl_pca;
	}

	public int getCl_tsne() {
		return cl_tsne;
	}

	public void setCl_tsne(int cl_tsne) {
		this.cl_tsne = cl_tsne;
	}

	public int getCl_k2() {
		return cl_k2;
	}

	public void setCl_k2(int cl_k2) {
		this.cl_k2 = cl_k2;
	}

	public int getCl_kn() {
		return cl_kn;
	}

	public void setCl_kn(int cl_kn) {
		this.cl_kn = cl_kn;
	}

    @CsvBindByName
	private float id;
    @CsvBindByName
	private float x_pca, y_pca, z_pca, x_tsne, y_tsne, z_tsne, x_k2, y_k2, z_k2;
    @CsvBindByName
    private int cl_pca, cl_tsne, cl_k2, cl_kn;

	public Point3D getPointPCA() {
		return new Point3D(x_pca, y_pca, z_pca);
	}

	public Point3D getPointTSNE() {
		return new Point3D(x_tsne, y_tsne, z_tsne);
	}

	@Override
	public String toString() {
		return "ClusterCenter [cl=" + cl + ", id=" + id + ", x_pca=" + x_pca + ", y_pca=" + y_pca + ", z_pca=" + z_pca
				+ ", x_tsne=" + x_tsne + ", y_tsne=" + y_tsne + ", z_tsne=" + z_tsne + ", x_k2=" + x_k2 + ", y_k2="
				+ y_k2 + ", z_k2=" + z_k2 + ", cl_pca=" + cl_pca + ", cl_tsne=" + cl_tsne + ", cl_k2=" + cl_k2
				+ ", cl_kn=" + cl_kn + "]";
	}

	public Point3D getPointK2() {
		return new Point3D(x_k2, y_k2, z_k2);
	}

	public Point3D getPointKind(String kind) {
		switch (kind.toLowerCase()) {
		case "pca": {
			return getPointPCA();
		}
		case "tsne": {
			return getPointTSNE();
		}
		case "k2": {
			return getPointK2();
		}
		}
		return null;
	}
}
