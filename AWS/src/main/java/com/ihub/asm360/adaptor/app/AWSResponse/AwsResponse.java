package com.ihub.asm360.adaptor.app.AWSResponse;

import com.ihub.asm360.adaptor.core.response.BaseResponse;

public class AwsResponse extends BaseResponse{

	/** The dynatrace result. */
	private DynatraceResult dynatraceResult;

	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public DynatraceResult getResult() {
		return dynatraceResult;
	}

	/**
	 * Sets the result.
	 *
	 * @param dynatraceResult the result to set
	 */
	public void setResult(DynatraceResult dynatraceResult) {
		this.dynatraceResult = dynatraceResult;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Response [result=" + dynatraceResult + "]";
	}
}
