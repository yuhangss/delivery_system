package com.example.delivery.common.facenet

/**
 * 人脸特征
 * 计算欧式距离
 */
class FaceFeatures {
    private val DIMS = 512
    var features: FloatArray

    init {
        features = FloatArray(DIMS)
    }

    fun getFea(): FloatArray {
        return features
    }


    fun setFea(fea: FloatArray) {
        this.features = fea
    }

    fun compare(faceFeatures: FaceFeatures): Double {
        var dist: Double = 0.0;
        for(i in 0 until DIMS) {
            dist += (features[i] - faceFeatures.features[i])*
                    (features[i] - faceFeatures.features[i])
        }
        dist = Math.sqrt(dist)
        return dist
    }
}