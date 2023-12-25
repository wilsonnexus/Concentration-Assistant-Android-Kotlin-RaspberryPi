package com.example.mykotlin

import android.app.admin.DevicePolicyManager
import android.os.Vibrator
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Mock
    private val devicePolicyManager: DevicePolicyManager? = null

    @Mock
    private val vibrator: Vibrator? = null
    private var mainFragment: MainFragment? = null
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this) // Initialize mocks

        // Create your MainFragment and inject the mocked dependencies
        mainFragment = MainFragment(devicePolicyManager, null, vibrator)
        // Optionally, you can set up any other dependencies or context here.
    }

    /*@Test
    public void defaultSpinnerSelection_shouldReturnCorrectValue() {
        Spinner spinner = mainFragment.getDurationSpinner();
        assertNotNull(spinner);

        // Set a default selection
        String[] durations = mainFragment.getResources().getStringArray(R.array.durations_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mainFragment.requireContext(), R.layout.custom_spinner_item, durations);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);

        String selection = spinner.getSelectedItem().toString();
        assertEquals("1 minute", selection);
    }*/
    @Test
    fun vibratorIsNotNull() {
        val vibrator = mainFragment!!.vibrator
        Assert.assertNotNull(vibrator)
    }

    @Test
    fun devicePolicyManagerIsNotNull() {
        val devicePolicyManager = mainFragment!!.devicePolicyManager
        Assert.assertNotNull(devicePolicyManager)
    }

    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(4, (2 + 2).toLong())
    }
}